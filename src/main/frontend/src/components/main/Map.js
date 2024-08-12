import React, {useContext, useEffect, useRef, useState} from 'react';
import {useNavermaps} from "react-naver-maps";
import {DataContext} from "../../contexts/DataContext";
import "../../css/Map.css";
import instance from "../main/axios/TokenInterceptor";

const Map = () => {
    const {totalDataArray, setTotalDataArray} = useContext(DataContext);
    const [AddressX, setAddressX] = useState(0);
    const [AddressY, setAddressY] = useState(0);
    const mapElement = useRef(null);
    const createMarkerList = useRef([]);     // 마커를 담을 배열
    const infoWindowList = useRef([]);          // 정보창을 담을 배열
    const [viewportWidth, setViewportWidth] = useState(window.innerWidth);  // 브라우저의 현재 너비
    const navermaps = useNavermaps();

    /**
     * DB에서 전체 일기를 가져와서 totalDataArray를 채우는 로직
     */
    useEffect(() => {
        const fetchData = async () => {
            setTotalDataArray([]);
            try {
                const response = await instance.get('http://localhost:8080/story/all');
                if (response.data.isSuccess) {
                    const storyInfoResponseDtos = response.data.result;

                    for (const dto of storyInfoResponseDtos) {
                        const {id, title, content, place, address, date} = dto;
                        await handleGeocode(id, title, content, place, address, date);
                    }
                } else {
                    console.error('일기 불러오기 오류');
                    console.log(response.data.code);
                    console.log(response.data.message);
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

    return (
        <div className="map-container">
            <div className="map-header">
                <p>일기 지도 🗺</p>
            </div>
            <div id='map' ref={mapElement} style={{width: '100%', height: '100%'}}>
            </div>
        </div>
    );
};

export default Map;
