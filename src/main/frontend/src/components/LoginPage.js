import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import axios from 'axios'; // axios 임포트
import '../css/LoginPage.css'; // 별도의 CSS 파일에서 스타일을 관리할 수 있습니다.
axios.defaults.withCredentials = true;

const LoginPage = message => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    // OAuth2 로그인
    const handleKakaoLoginClick = () => {
        window.location.href = 'http://localhost:8080/oauth/kakao';
    };

    const handleNaverLoginClick = () => {
        window.location.href = 'http://localhost:8080/oauth/naver';
    };

    const handleGoogleLoginClick = () => {
        window.location.href = 'http://localhost:8080/oauth/google';
    };

    // 일반 JSON 로그인
    const handleLogin = async () => {
        try {
            const response = await axios.post('http://localhost:8080/login', {
                email,
                password
            });
            const accessToken = response.headers['authorization'] || response.headers['Authorization'];
            // 로그인 성공 시 대시보드 페이지로 이동
            if (accessToken) {
                console.log("Access Token:", accessToken);
                alert("로그인 성공 : " + accessToken);
                // 예: 토큰을 로컬 스토리지에 저장하거나 상태에 저장
                localStorage.setItem('accessToken', accessToken);
                navigate("/main/map"); // 로그인 성공 시 페이지 이동
            } else {
                console.error("토큰이 응답에 포함되어 있지 않습니다.");
            }
        } catch (error) {
            console.error("로그인 실패:", error);
        }
    };

    const handleSignUp = () => {
        // 회원가입 페이지로 이동
        navigate("/sign-up");
    };

    return (
        <div className="login-container">
            <h2>Travel</h2>
            <div className="login-form">
                <input
                    type="text"
                    placeholder="아이디"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="login-input"
                />
                <input
                    type="password"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="login-input"
                />
                <button onClick={handleLogin} className="login-button">
                    로그인
                </button>
                <button onClick={handleSignUp} className="sign-up-button">
                    회원가입
                </button>
            </div>
            <div className="social-login-buttons">
                <img
                    src="/images/kakao-icon.png"
                    alt="Kakao Login"
                    className="social-icon"
                    onClick={handleKakaoLoginClick}
                />
                <img
                    src="/images/naver-icon.png"
                    alt="Naver Login"
                    className="social-icon"
                    onClick={handleNaverLoginClick}
                />
                <img
                    src="/images/google-icon.png"
                    alt="Google Login"
                    className="social-icon"
                    onClick={handleGoogleLoginClick}
                />
            </div>
        </div>
    );
};

export default LoginPage;
