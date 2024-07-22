import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../css/OauthUserInfoPage.css';

const OauthUserInfoPage = () => {
    const accessToken = localStorage.getItem('accessToken'); // Adjust this line based on where you store the accessToken
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [birth, setBirth] = useState('');
    const [gender, setGender] = useState('');
    const [age, setAge] = useState('');
    const [location, setLocation] = useState('');
    const [dtype, setDtype] = useState('');
    const [role, setRole] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await axios.get('http://localhost:8080/user/info', {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    },
                    withCredentials: true
                });

                const { email, name, birth, gender, age, location, dtype, role } = response.data;
                setEmail(email || '');
                setName(name || '');
                setBirth(birth || '');
                setGender(gender || '');
                setAge(age || '');
                setLocation(location || '');
                setDtype(dtype || '');
                setRole(role || '');

            } catch (error) {
                if (error.response && error.response.data) {
                    setError(error.response.data);
                } else {
                    setError("사용자 정보를 불러오는 데 실패했습니다: 서버와의 통신에 실패했습니다.");
                }
                console.error("사용자 정보 불러오기 실패:", error.response ? error.response.data : error.message);
            }
        };

        fetchUserInfo();
    }, []);

    const handleUserInfo = async () => {
        try {
            const response = await axios.post('http://localhost:8080/user/info', {
                email,
                name,
                birth,
                gender,
                age,
                location
            }, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });

            console.log("정보 수정 성공", response.data);
            alert("정보 수정 성공!");
            navigate("/"); // 홈 페이지로 이동
        } catch (error) {
            if (error.response && error.response.data) {
                setError(error.response.data);
            } else {
                setError("정보 수정 실패: 서버와의 통신에 실패했습니다.");
            }
            console.error("정보 수정 실패:", error.response ? error.response.data : error.message);
        }
    };

    return (
        <div className="userinfo-container">
            <h2>개인정보</h2>
            <div className="userinfo-form">
                {error && <div className="error-message">{error}</div>}
                <div className="userinfo-field">
                    <label>이메일</label>
                    <input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>이름</label>
                    <input
                        type="text"
                        placeholder="이름"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>생년월일</label>
                    <input
                        type="date"
                        placeholder="생년월일"
                        value={birth}
                        onChange={(e) => setBirth(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>성별</label>
                    <select
                        value={gender}
                        onChange={(e) => setGender(e.target.value)}
                        className="userinfo-input"
                    >
                        <option value="">성별</option>
                        <option value="M">남성</option>
                        <option value="F">여성</option>
                    </select>
                </div>
                <div className="userinfo-field">
                    <label>나이</label>
                    <input
                        type="number"
                        placeholder="나이"
                        value={age}
                        onChange={(e) => setAge(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <div className="userinfo-field">
                    <label>사는 지역</label>
                    <input
                        type="text"
                        placeholder="사는 지역"
                        value={location}
                        onChange={(e) => setLocation(e.target.value)}
                        className="userinfo-input"
                    />
                </div>
                <button onClick={handleUserInfo} className="userinfo-button">
                    설정 완료
                </button>
            </div>
        </div>
    );
};

export default OauthUserInfoPage;
