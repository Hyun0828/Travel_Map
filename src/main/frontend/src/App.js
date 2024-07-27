import React from 'react';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import LoginPage from './components/LoginPage';
import SignUpPage from './components/SignUpPage';
import OauthUserInfoPage from './components/oauth/OauthUserInfoPage';
import KakaoRedirectPage from './components/oauth/KakaoRedirectPage';
import NaverRedirectPage from './components/oauth/NaverRedirectPage';
import GoogleRedirectPage from './components/oauth/GoogleRedirectPage';
import Sidebar from "./components/main/Sidebar";
import MainContent from "./components/main/MainContent"
import {DataProvider} from './contexts/DataContext';
import './App.css';

const App = () => {
    return (
        <div className="App">
            <DataProvider>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<LoginPage/>}/>
                        <Route path="/sign-up" element={<SignUpPage/>}/>
                        <Route path="/oauth/user/info" element={<OauthUserInfoPage/>}/>
                        <Route path="/login/oauth2/code/kakao" element={<KakaoRedirectPage/>}/>
                        <Route path="/login/oauth2/code/naver" element={<NaverRedirectPage/>}/>
                        <Route path="/login/oauth2/code/google" element={<GoogleRedirectPage/>}/>
                        <Route
                            path="/main/*"
                            element={
                                <div className="layout">
                                    <Sidebar/>
                                    <div className="main-content">
                                        <MainContent/>
                                    </div>
                                </div>
                            }
                            />
                    </Routes>
                </BrowserRouter>
            </DataProvider>
        </div>
    );
};

export default App;
