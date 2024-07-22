import React, {useEffect} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import axios from 'axios';

const KakaoRedirectPage = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const handleOAuthKakao = async (code) => {
        try {
            // 카카오로부터 받아온 code를 서버에 전달하여 카카오로 회원가입 & 로그인한다
            const response = await axios.get(`http://localhost:8080/oauth/login/kakao?code=${code}`, {
                withCredentials: true // 서버에서 쿠키를 설정할 수 있도록 허용
            });
            // 응답 헤더에서 AccessToken 추출
            const accessToken = response.headers['Authorization'] || response.headers['authorization'];
            const role = response.data;

            alert("로그인 성공: " + role);
            localStorage.setItem('accessToken', accessToken);

            if (role === "GUEST") {
                navigate("/oauth/user/info");
            } else if (role === "USER") {
                navigate("/success"); // 메인페이지로 이동
            }
        } catch (error) {
            console.error("로그인 실패", error);
            navigate("/fail");
        }
    };

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const code = searchParams.get('code');  // 카카오는 Redirect 시키면서 code를 쿼리 스트링으로 준다.
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

export default KakaoRedirectPage;