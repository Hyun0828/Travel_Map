import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../css/SignUpPage.css';

const SignUpPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [birth, setBirth] = useState('');
    const [gender, setGender] = useState('');
    const [age, setAge] = useState('');
    const [location, setLocation] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSignUp = async () => {
        try {
            const response = await axios.post('http://localhost:8080/sign-up', {
                email,
                password,
                name,
                birth,
                gender,
                age,
                location
            });
            console.log("회원가입 성공", response.data);
            alert("회원가입 성공!");
            navigate("/"); // 로그인 페이지로 이동
        } catch (error) {
            if (error.response && error.response.data) {
                setError(error.response.data);
            } else {
                setError("회원가입 실패: 서버와의 통신에 실패했습니다.");
            }
            console.error("회원가입 실패:", error.response ? error.response.data : error.message);
        }
    };

    return (
        <div className="signup-container">
            <h2>회원가입</h2>
            <div className="signup-form">
                {error && <div className="error-message">{error}</div>}
                <div className="signup-field">
                    <label>이메일</label>
                    <input
                        type="email"
                        placeholder="이메일"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="signup-input"
                    />
                </div>
                <div className="signup-field">
                    <label>비밀번호</label>
                    <input
                        type="password"
                        placeholder="비밀번호"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="signup-input"
                    />
                </div>
                <div className="signup-field">
                    <label>이름</label>
                    <input
                        type="text"
                        placeholder="이름"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="signup-input"
                    />
                </div>
                <div className="signup-field">
                    <label>생년월일</label>
                    <input
                        type="date"
                        placeholder="생년월일"
                        value={birth}
                        onChange={(e) => setBirth(e.target.value)}
                        className="signup-input"
                    />
                </div>
                <div className="signup-field">
                    <label>성별</label>
                    <select
                        value={gender}
                        onChange={(e) => setGender(e.target.value)}
                        className="signup-input"
                    >
                        <option value="">성별</option>
                        <option value="male">남성</option>
                        <option value="female">여성</option>
                    </select>
                </div>
                <div className="signup-field">
                    <label>나이</label>
                    <input
                        type="number"
                        placeholder="나이"
                        value={age}
                        onChange={(e) => setAge(e.target.value)}
                        className="signup-input"
                    />
                </div>
                <div className="signup-field">
                    <label>사는 지역</label>
                    <input
                        type="text"
                        placeholder="사는 지역"
                        value={location}
                        onChange={(e) => setLocation(e.target.value)}
                        className="signup-input"
                    />
                </div>
                <button onClick={handleSignUp} className="signup-button">
                    회원가입
                </button>
            </div>
        </div>
    );
};

export default SignUpPage;