import React, {useContext, useEffect, useRef, useState} from 'react';
import axios from "axios";
import {Overlay, useMap, useNavermaps} from "react-naver-maps";
import {DataContext} from "../../contexts/DataContext";
import "../../css/Map.css";
import MarkerClustering from "../main/cluster/MarkerClustering";


axios.defaults.withCredentials = true;

const Map = () => {
    const {totalDataArray, setTotalDataArray} = useContext(DataContext);
    const [AddressX, setAddressX] = useState(0);
    const [AddressY, setAddressY] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState('');
    const mapElement = useRef(null);
    const createMarkerList = useRef([]);     // 마커를 담을 배열
    const infoWindowList = useRef([]);          // 정보창을 담을 배열
    const [viewportWidth, setViewportWidth] = useState(window.innerWidth);  // 브라우저의 현재 너비
    const navermaps = useNavermaps();

    const accessToken = localStorage.getItem('accessToken');

    /**
     * DB에서 전체 일기를 가져와서 totalDataArray를 채우는 로직
     */
    useEffect(() => {
        const fetchData = async () => {
            setTotalDataArray([]);
            try {
                const response = await axios.get("http://localhost:8080/story/all", {
                    headers: {
                        'Authorization': `Bearer ${accessToken}`
                    }
                });
                const storyInfoResponseDtos = response.data;

                for (const dto of storyInfoResponseDtos) {
                    const {id, title, content, place, address, date} = dto;
                    await handleGeocode(id, title, content, place, address, date);
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData();
    }, []);

    /**
     * 위도, 경도 추출하고 데이터 저장
     */
    const handleGeocode = (id, title, content, place, address, date) => {
        if (!navermaps || !navermaps.Service) {
            console.error('Naver Maps service is not available');
            return;
        }

        navermaps.Service.geocode(
            {address: address},
            (status, response) => {
                if (status !== navermaps.Service.Status.OK) {
                    console.error('Geocoding error:', status);
                    return alert('Something went wrong during geocoding.');
                }

                const result = response.result;
                const items = result.items;
                if (items.length > 0) {
                    const {x: lng, y: lat} = items[0].point;

                    const newData = {
                        dom_id: id,
                        title: title,
                        content: content,
                        place: place,
                        address: address,
                        date: date,
                        lat: lat,
                        lng: lng
                    };
                    setTotalDataArray(prevData => [...prevData, newData]);
                } else {
                    console.error('No geocoding results found.');
                    alert('No geocoding results found.');
                }
            }
        );
    };


    /**
     * 사용자의 현재 위치 가져오기
     */
    useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
                const {latitude, longitude} = position.coords;
                setAddressX(longitude);
                setAddressY(latitude);
            });
        } else {
            console.error('Geolocation is not supported by this browser.');
        }
    }, []);

    /**
     * 검색 키워드로 좌표 검색
     */
    useEffect(() => {
        if (searchKeyword) {
            window.naver.maps.Service.geocode(
                {query: searchKeyword},
                function (status, res) {
                    if (res.v2.addresses.length === 0) {
                        console.error('No addresses found for the given keyword');
                    } else {
                        const resAddress = res.v2.addresses[0];
                        const x = parseFloat(resAddress.x);
                        const y = parseFloat(resAddress.y);
                        setAddressX(x);
                        setAddressY(y);
                    }
                }
            );
        }
    }, [searchKeyword]);

    /**
     * 지도 초기화 및 업데이트
     */
    useEffect(() => {
        if (!mapElement.current || !window.naver) return;

        const center = new window.naver.maps.LatLng(AddressY, AddressX);

        const mapOptions = {
            center: center,
            zoom: 17,
            minZoom: 11,
            maxZoom: 19,
            zoomControl: true,
            zoomControlOptions: {
                style: window.naver.maps.ZoomControlStyle.SMALL,
                position: window.naver.maps.Position.TOP_RIGHT,
            },
            mapDataControl: false,
            scaleControl: false,
        };

        // 지도 생성
        mapElement.current = new window.naver.maps.Map("map", mapOptions);
        // 마커 추가
        addMarkers();
        // 검색 결과 거리순으로 재정렬
        // resetListHandler();
    }, [AddressX, AddressY, viewportWidth, totalDataArray]);

    /**
     * 검색 키워드 입력 처리
     */
    const handleSearch = (e) => {
        e.preventDefault();
        const keyword = e.target.elements.keyword.value;
        setSearchKeyword(keyword);
    };

    /**
     * 지도가 새롭게 그려질 때 이벤트 리스너를 등록하고 컴포넌트가 언마운트될 때 이벤트 리스너 해제
     * idle : 지도 움직임이 멈추었을 때 이벤트 발생
     */
    useEffect(() => {
        if (mapElement.current) {
            const MoveEventListener = window.naver.maps.Event.addListener(
                mapElement.current,
                'idle',
                idleHandler
            );
            return () => {
                window.naver.maps.Event.removeListener(MoveEventListener);
            };
        }
    }, [mapElement.current]);

    /**
     * 컴포넌트가 렌더링될 때 한 번만 실행
     * 화면 크기가 바뀌면 handleResize 함수 실행
     * 브라우저 창 크기 변경 이벤트를 감지하고 이벤트가 발생하면 handleResize 함수 실행
     * return 되면서 이벤트 리스너를 정리하고 제거
     */
    useEffect(() => {
        const handleResize = () => {
            setViewportWidth(window.innerWidth);
        };
        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);


    /**
     * 지도가 움직이다가 멈추면 updateMarkers 호출
     * 파라미터로 새로운 지도와 마커 리스트를 준다.
     */
    const idleHandler = () => {
        updateMarkers(mapElement.current, createMarkerList.current);
    };

    /**
     * 마커가 현재 보이는 영역에 있는지 확인하고 보이면 showMarker, 숨겨져 있으면 hideMarker 호출
     */
    const updateMarkers = (map, markers) => {
        if (!map || !Array.isArray(markers)) return;

        const mapBounds = map.getBounds();
        markers.forEach((marker) => {
            const position = marker.getPosition();
            if (mapBounds.hasPoint(position)) {
                showMarker(map, marker);
            } else {
                hideMarker(marker);
            }
        });
    };

    /**
     * 지도에 마커를 표시하는 함수
     */
    const showMarker = (map, marker) => {
        // 지도에 표시되어있는지 확인
        if (marker.getMap()) return;
        // 표시되어있지 않다면 오버레이를 지도에 추가
        marker.setMap(map);
    };

    /**
     * 지도에서 마커를 숨기는 함수
     */
    const hideMarker = (marker) => {
        // 지도에 표시되어있는지 확인
        if (!marker.getMap()) return;
        // 표시되어있다면 오버레이를 지도에서 삭제
        marker.setMap(null);
    };


    const getClickHandler = (index) => {
        return () => {
            if (infoWindowList.current[index].getMap())
                infoWindowList.current[index].close();
            else if (mapElement.current != null)
                infoWindowList.current[index].open(mapElement.current, createMarkerList.current[index]);
        }
    };

    /**
     * 마커를 생성하고 createMarkerList에 추가
     */
    const addMarker = async (dom_id, title, content, place, address, date, lat, lng) => {
        try {
            const newMarker = new window.naver.maps.Marker({
                position: new window.naver.maps.LatLng(lat, lng),
                map: mapElement.current,
                title: place,
                clickable: true,
            });
            createMarkerList.current.push(newMarker);

            const contentString = `
                            <div style="
                padding: 15px;
                width: 250px;
                border: 1px solid #ddd;
                border-radius: 8px;
                background-color: #f9f9f9;
                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            ">
                <h3 style="
                    margin-top: 0;
                    font-size: 1.2em;
                    color: #333;
                    font-family: 'Arial', sans-serif;
                ">${title}</h3>
                
                <p style="
                    margin: 5px 0;
                    font-size: 0.9em;
                    color: #555;
                    font-family: 'Arial', sans-serif;
                "><strong>장소:</strong> ${place}</p>
                
                <p style="
                    margin: 5px 0;
                    font-size: 0.9em;
                    color: #555;
                    font-family: 'Arial', sans-serif;
                "><strong>주소:</strong> ${address}</p>
                
                <p style="
                    margin: 5px 0;
                    font-size: 0.9em;
                    color: #555;
                    font-family: 'Arial', sans-serif;
                "><strong>날짜:</strong> ${date}</p>
                
                <p style="
                    margin: 10px 0;
                    font-size: 0.9em;
                    color: #333;
                    font-family: 'Arial', sans-serif;
                ">${content}</p>
                
                <a href="/main/story/${dom_id}" style="
                    display: inline-block;
                    padding: 8px 12px;
                    margin-top: 10px;
                    font-size: 0.9em;
                    color: #007bff;
                    text-decoration: none;
                    border-radius: 4px;
                    border: 1px solid #007bff;
                    background-color: #ffffff;
                    transition: background-color 0.3s, color 0.3s;
                " onmouseover="this.style.backgroundColor='#007bff'; this.style.color='#ffffff';" onmouseout="this.style.backgroundColor='#ffffff'; this.style.color='#007bff';">
                    자세히 보기
                </a>
            </div>
            `;

            const infoWindow = new window.naver.maps.InfoWindow({
                content: contentString,
                maxWidth: 300,
            });

            infoWindowList.current.push(infoWindow);

        } catch (error) {
            console.error('Error fetching story:', error);
        }
    };

    /**
     * 마커 생성 함수 + 마커 클릭 핸들러 설정
     */
    const addMarkers = () => {
        if (!Array.isArray(totalDataArray) || totalDataArray.length === 0) {
            console.warn('No data available to add markers.');
            return;
        }

        createMarkerList.current = [];
        infoWindowList.current = [];

        totalDataArray.forEach((markerObj) => {
            const {dom_id, title, content, place, address, date, lat, lng} = markerObj;
            addMarker(dom_id, title, content, place, address, date, lat, lng);
        });

        for (let i = 0; i < createMarkerList.current.length; i++) {
            window.naver.maps.Event.addListener(createMarkerList.current[i], "click", getClickHandler(i));
        }
    };

    useEffect(() => {
        resetListHandler();
    }, [mapElement.current]);

    // 리셋 버튼 핸들러
    const resetListHandler = () => {
        if (!mapElement.current) return;
        const newArray = [...totalDataArray].sort((a, b) => {
            const currentCenterLatLng = mapElement.current.getCenter();
            const LatLngA = new window.naver.maps.LatLng(a.lat, a.lng);
            const LatLngB = new window.naver.maps.LatLng(b.lat, b.lng);
            const projection = mapElement.current.getProjection();
            const distanceA = projection.getDistance(currentCenterLatLng, LatLngA);
            const distanceB = projection.getDistance(currentCenterLatLng, LatLngB);

            if (distanceA < distanceB) return -1;
            else if (distanceA > distanceB) return 1;
            else return 0;
        });

        // setSortedDomData(newArray);
    };


    // const MarkerCluster = () => {
    //
    //     const {htmlMarker1, htmlMarker2, htmlMarker3, htmlMarker4, htmlMarker5} = useGetClusterIcon(navermaps); // 클러스트 아이콘 DOM 리스트
    //
    //     const getCluster = () => {
    //
    //         const cluster = new MarkerClustering({
    //             minClusterSize: 2,
    //             maxZoom: 14, // 조절하면 클러스터링이 되는 기준이 달라짐 (map zoom level)
    //             map: mapElement.current,
    //             markers: createMarkerList.current,
    //             disableClickZoom: false,
    //             gridSize: 120,
    //             icons: [htmlMarker1, htmlMarker2, htmlMarker3, htmlMarker4, htmlMarker5],
    //             indexGenerator: [5, 10, 15, 20, 30],
    //             stylingFunction: function (clusterMarker, count) {
    //                 clusterMarker.getElement().querySelector('div:first-child').innerText = count;
    //             },
    //         });
    //
    //         return cluster;
    //     };
    //
    //     const [cluster] = useState(getCluster());
    //
    //     return <Overlay element={cluster}/>;
    // }
    //
    // /**
    //  * 마커 클러스터링 아이콘 생성
    //  */
    // const useGetClusterIcon = (navermaps) => {
    //     const createClusterIcon = (url) => ({
    //         content: `<div style="cursor:pointer;width:40px;height:40px;line-height:42px;font-size:10px;color:white;text-align:center;font-weight:bold;background:url(${url});background-size:contain;"></div>`,
    //         size: navermaps.Size(40, 40),
    //         anchor: navermaps.Point(20, 20),
    //     });
    //
    //     return {
    //         htmlMarker1: createClusterIcon('https://navermaps.github.io/maps.js.ncp/docs/img/cluster-marker-1.png'),
    //         htmlMarker2: createClusterIcon('https://navermaps.github.io/maps.js.ncp/docs/img/cluster-marker-2.png'),
    //         htmlMarker3: createClusterIcon('https://navermaps.github.io/maps.js.ncp/docs/img/cluster-marker-3.png'),
    //         htmlMarker4: createClusterIcon('https://navermaps.github.io/maps.js.ncp/docs/img/cluster-marker-4.png'),
    //         htmlMarker5: createClusterIcon('https://navermaps.github.io/maps.js.ncp/docs/img/cluster-marker-5.png'),
    //     };
    // };

    return (
        <div className="map-container">
            <div className="map-header">
                <p>일기 지도 🗺</p>
            </div>
            {/*<form onSubmit={handleSearch}>*/}
            {/*    <input type="text" name="keyword" placeholder="Enter a location"/>*/}
            {/*    <button type="submit">Search</button>*/}
            {/*</form>*/}
            {/*<button onClick={() => resetListHandler()}>*/}
            {/*    Reset List*/}
            {/*</button>*/}
            <div id='map' ref={mapElement} style={{width: '100%', height: '100%'}}>
                {/*<MarkerCluster/>*/}
            </div>
        </div>
    );
};

export default Map;
