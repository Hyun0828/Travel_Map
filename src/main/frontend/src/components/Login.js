import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Login.css';

const Login = () => {
    const navigate = useNavigate();

    const handleSignupClick = () => {
        navigate('/signup');
    };

    return (
        <div className="container">
            <h1 style={{ color: '#fff' }}>로그인</h1>
            <form className="form">
                <label className="label">이메일</label>
                <input type="email" className="input" />
                <label className="label">비밀번호</label>
                <input type="password" className="input" />
                <button type="submit" className="button button-submit">로그인</button>
            </form>
            <button onClick={handleSignupClick} className="button button-signup">회원가입</button>
            <a href="http://localhost:8080/oauth2/authorization/google" className="oauth-button google-button">
                <img src="/images/google-icon.png" alt="Google" className="oauth-icon"/>
            </a>
            <a href="http://localhost:8080/oauth2/authorization/kakao" className="oauth-button kakao-button">
                <img src="/images/kakao-icon.png" alt="Kakao" className="oauth-icon"/>
            </a>
            <a href="http://localhost:8080/oauth2/authorization/naver" className="oauth-button naver-button">
                <img src="/images/naver-icon.png" alt="Naver" className="oauth-icon"/>
            </a>
        </div>
    );
};

export default Login;
