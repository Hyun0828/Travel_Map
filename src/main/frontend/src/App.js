import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from "./components/LoginPage";
import KakaoRedirectPage from "./components/KakaoRedirectPage";
import NaverRedirectPage from "./components/NaverRedirectPage";
import GoogleRedirectPage from "./components/GoogleRedirectPage";


const App = () => {
    return (
        <div className='App'>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<LoginPage />}></Route>
                    <Route path="/login/oauth2/code/kakao" element={<KakaoRedirectPage />}></Route>
                    <Route path="/login/oauth2/code/naver" element={<NaverRedirectPage />}></Route>
                    <Route path="/login/oauth2/code/google" element={<GoogleRedirectPage/>}></Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
};

export default App;