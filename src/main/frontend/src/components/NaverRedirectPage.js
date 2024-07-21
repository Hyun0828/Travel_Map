import React, {useEffect} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import axios from 'axios';

const NaverRedirectPage = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const handleOAuthKakao = async (code) => {
        try {
            // 네이버로부터 받아온 code를 서버에 전달하여 네이버로 회원가입 & 로그인한다
            const response = await axios.get(`http://localhost:8080/oauth/login/naver?code=${code}`, {
                withCredentials: true // 서버에서 쿠키를 설정할 수 있도록 허용
            });
            const accessToken = response.headers['Authorization'] || response.headers['authorization'];
            // AccessToken 출력
            console.log("Access Token: " + accessToken);
            alert("로그인 성공: " + accessToken);

            // navigate("/success"); // 메인페이지로 이동
        } catch (error) {
            navigate("/fail");
        }
    };

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const code = searchParams.get('code');  // 네이버는 Redirect 시키면서 code를 쿼리 스트링으로 준다.
        if (code) {
            alert("CODE = " + code)
            handleOAuthKakao(code);
        }
    }, [location]);

    return (
        <div>
            <div>Processing...</div>
        </div>
    );
};

export default NaverRedirectPage;