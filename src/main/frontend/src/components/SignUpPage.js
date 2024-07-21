import React, { useState } from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';
import '../css/SignUpPage.css'; 

const SignUpPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [birth, setBirth] = useState('');
    const [gender, setGender] = useState('');
    const [age, setAge] = useState('');
    const [location, setLocation] = useState('');
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
            }, {
                headers: {

                }
            });

            console.log("회원가입 성공", response.data);
            // 회원가입 성공 시 리다이렉션 또는 다른 처리
            navigate("/"); // 로그인 페이지로 이동
        } catch (error) {
            console.error("회원가입 실패:", error.response ? error.response.data : error.message);
        }
    };

    return (
        <div className="signup-container">
            <div className="signup-form">
                <input
                    type="email"
                    placeholder="이메일"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="signup-input"
                />
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="signup-input"
                />
                <input
                    type="text"
                    placeholder="이름"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="signup-input"
                />
                <input
                    type="date"
                    placeholder="생년월일"
                    value={birth}
                    onChange={(e) => setBirth(e.target.value)}
                    className="signup-input"
                />
                <select
                    value={gender}
                    onChange={(e) => setGender(e.target.value)}
                    className="signup-input"
                >
                    <option value="">성별</option>
                    <option value="male">남성</option>
                    <option value="female">여성</option>
                </select>
                <input
                    type="number"
                    placeholder="나이"
                    value={age}
                    onChange={(e) => setAge(e.target.value)}
                    className="signup-input"
                />
                <input
                    type="text"
                    placeholder="사는 지역"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    className="signup-input"
                />
                <button onClick={handleSignUp} className="signup-button">
                    회원가입
                </button>
            </div>
        </div>
    );
};

export default SignUpPage;
