import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../css/Sidebar.css'
axios.defaults.withCredentials = true;

const Sidebar = () => {
    const [isSidebarVisible, setIsSidebarVisible] = useState(true);
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            // 로그아웃 API 호출
            await axios.post('http://localhost:8080/logout', {}, {
            }); // 로그아웃 API 엔드포인트에 맞게 수정
            localStorage.removeItem('accessToken');
            navigate('/'); // 로그인 페이지 이동
        } catch (error) {
            console.error('로그아웃 오류 발생:', error);
        }
    };

    const toggleSidebar = () => {
        setIsSidebarVisible(!isSidebarVisible);
    };

    return (
        <div className={`sidebar ${isSidebarVisible ? 'visible' : 'hidden'}`}>
            <button className="toggle-button" onClick={toggleSidebar}>
                {isSidebarVisible ? '◄' : '►'}
            </button>
            {isSidebarVisible && (
                <div className="sidebar-content">
                    <Link to="/main/map">지도</Link>
                    <Link to="/main/storyList?page=1">일기</Link>
                    <Link to="/main/write">일기 작성</Link>
                    <Link to="/main/calendar">달력</Link>
                    <Link to="/main/settings">개인정보 설정</Link>
                    <div className="logout-wrapper">
                        <button onClick={handleLogout} className="logout-button">
                            로그아웃
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Sidebar;
