import React, {useState} from 'react';
import axios from 'axios';
import {useNavigate} from 'react-router-dom';
import '../css/SignUpPage.css';

axios.defaults.withCredentials = true;

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

            if (response.data.isSuccess) {
                alert("회원가입 성공!");
                navigate("/"); // 로그인 페이지로 이동
            } else {
                // 성공이 아니지만 통신 에러는 발생하지 않은 경우 처리
                setError(response.data.message || "회원가입 실패: 알 수 없는 오류가 발생했습니다.");
                console.error("회원가입 실패:", response.data);
            }
        } catch (error) {
            if (error.response && error.response.data) {
                // 서버로부터 응답이 있는 경우
                const errorData = error.response.data;
                if (errorData.isSuccess === false) {
                    // isSuccess가 false인 경우에 대한 처리
                    setError(errorData.message || "회원가입 실패: 알 수 없는 오류가 발생했습니다.");
                } else {
                    setError("회원가입 실패: 서버에서 잘못된 응답을 받았습니다.");
                }

                console.error("회원가입 실패:", errorData);
            } else {
                // 서버로부터 응답이 없는 경우 또는 다른 오류
                setError("회원가입 실패: 서버와의 통신에 실패했습니다.");
                console.error("회원가입 실패:", error.message);
            }
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
                        <option value="M">남성</option>
                        <option value="F">여성</option>
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
