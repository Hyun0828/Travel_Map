import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

const GoogleRedirectPage = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const handleOAuthGoogle = async (code) => {
        try {
            const response = await axios.get(`http://localhost:8080/oauth/login/google?code=${code}`, {
                withCredentials: true // 서버에서 쿠키를 설정할 수 있도록 허용
            });
            // 응답 헤더에서 AccessToken 추출
            const accessToken = response.headers['Authorization'] || response.headers['authorization'];

            // AccessToken 출력
            console.log("Access Token: " + accessToken);
            alert("로그인 성공: " + accessToken);

            // navigate("/success"); // 메인페이지로 이동
        } catch (error) {
            console.error("로그인 실패", error);
            navigate("/fail");
        }
    };

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const code = searchParams.get('code');  // 구글은 Redirect 시키면서 code를 쿼리 스트링으로 준다.
        if (code) {
            console.log("CODE = " + code);
            handleOAuthGoogle(code);
        }
    }, [location]);

    return (
        <div>
            <div>Processing...</div>
        </div>
    );
};

export default GoogleRedirectPage;
