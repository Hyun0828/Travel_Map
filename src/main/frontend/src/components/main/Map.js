import React, {useContext, useEffect, useRef, useState} from 'react';
import CustomMapMarker from "./CustomMapMarker";
import {DataContext} from "../../contexts/DataContext";

const Map = () => {
    const {totalDataArray, setTotalDataArray} = useContext(DataContext); // 추가
    const [AddressX, setAddressX] = useState(0);
    const [AddressY, setAddressY] = useState(0);
    const [searchKeyword, setSearchKeyword] = useState('');
    const mapElement = useRef(null);
    const [newMap, setNewMap] = useState(null);
    const createMarkerList = useRef([]);     // 마커를 담을 배열
    const [viewportWidth, setViewportWidth] = useState(window.innerWidth);  // 브라우저의 현재 너비

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
        const map = new window.naver.maps.Map(mapElement.current, mapOptions);
        setNewMap(map);

        console.log("Total Data Array:", totalDataArray);

        // 초기 마커 추가
        new window.naver.maps.Marker({
            map: map,
            position: center,
        });

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
        if (newMap) {
            const MoveEventListener = window.naver.maps.Event.addListener(
                newMap,
                'idle',
                idleHandler
            );
            return () => {
                window.naver.maps.Event.removeListener(MoveEventListener);
            };
        }
    }, [newMap]);

    /**
     * 컴포넌트가 렌더링될 때 한 번만 실행
     * 화면 크기가 바뀌면 handleResize 함수 실행
     * 브라우저 창 크기 변경 이벤트를 감지하고 이벤트가 발생하면 handleResize 함수 실행
     * return 되면서 이벤트 리스너를 정리하고 제거
     */
    useEffect(() => {
        const handleResize = () => {
            console.log("viewportwidth : " + viewportWidth);
            setViewportWidth(window.innerWidth);
        };
        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
        };
    }, []);


    /**
     * 지도가 움직이면 updateMarkers 호출
     * 파라미터로 새로운 지도와 마커 리스트를 준다.
     */
    const idleHandler = () => {
        updateMarkers(newMap, createMarkerList);
    };

    /**
     * 마커가 현재 보이는 영역에 있는지 확인하고 보이면 showMarker, 숨겨져 있으면 hideMarker 호출
     */
    const updateMarkers = (map, markers) => {
        if (!map || !Array.isArray(markers)) return;

        console.log("markers : ")
        console.log(markers)
        const mapBounds = map.getBounds();
        markers.forEach(marker => {
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

    /**
     * 마커를 생성하고 createMarkerList에 추가
     */
    const addMarker = (id, name, lat, lng) => {
        try {
            console.log(`Adding marker: id=${id}, name=${name}, lat=${lat}, lng=${lng}`);
            const newMarker = new window.naver.maps.Marker({
                position: new window.naver.maps.LatLng(lng, lat),
                map: newMap,
                title: name,
                clickable: true,
                // 커스텀 마커
                // icon: {
                //     // html element를 반환하는 CustomMapMarker 컴포넌트 할당
                //     content: CustomMapMarker({title: name, windowWidth: viewportWidth}),
                //     // 마커의 크기 지정
                //     size: new window.naver.maps.Size(38, 58),
                //     // 마커의 기준위치 지정
                //     anchor: new window.naver.maps.Point(19, 58),
                // },
            });
            // 마커 리스트에 추가
            createMarkerList.current.push(newMarker);
            console.log(createMarkerList)
            // 마커에 이벤트 핸들러 등록
            window.naver.maps.Event.addListener(newMarker, 'click', () =>
                markerClickHandler(id)
            );
        } catch (e) {
            console.error('Error adding marker:', e);
        }
    }

    // 마커 클릭 핸들러
    const markerClickHandler = (id) => {
        // navigate(`/ground/${id}`); // 주석을 제거하고 navigate 함수 구현
        console.log(`Marker clicked: ${id}`);
    };

    // 마커 생성 함수
    const addMarkers = () => {
        if (!Array.isArray(totalDataArray) || totalDataArray.length === 0) {
            console.warn('No data available to add markers.');
            return;
        }

        totalDataArray.forEach(markerObj => {
            const {dom_id, title, lat, lng} = markerObj;
            addMarker(dom_id, title, lat, lng);
        });
    };

    // 리셋 버튼 핸들러
    const resetListHandler = () => {
        if (!newMap) return;
        const newArray = [...totalDataArray].sort((a, b) => {
            const currentCenterLatLng = newMap.getCenter();
            const LatLngA = new window.naver.maps.LatLng(a.lat, a.lng);
            const LatLngB = new window.naver.maps.LatLng(b.lat, b.lng);
            const projection = newMap.getProjection();
            const distanceA = projection.getDistance(currentCenterLatLng, LatLngA);
            const distanceB = projection.getDistance(currentCenterLatLng, LatLngB);

            if (distanceA < distanceB) return -1;
            else if (distanceA > distanceB) return 1;
            else return 0;
        });

        // setSortedDomData(newArray);
    };

    return (
        <div className="map-container">
            <form onSubmit={handleSearch}>
                <input type="text" name="keyword" placeholder="Enter a location" />
                <button type="submit">Search</button>
            </form>
            <button onClick={() => resetListHandler()}>
                Reset List
            </button>
            <div ref={mapElement} style={{ width: '100%', height: '100%' }} />
        </div>
    );
};

export default Map;
