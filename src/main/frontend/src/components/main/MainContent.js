import React from 'react';
import {Route, Routes} from 'react-router-dom';
import Map from './Map';
import Settings from './Settings';
import Calendar from './Calendar';
import Story from "./Story";
import StoryList from "./StoryList"

const MainContent = () => {

    return (
        <div className="main-content">
            <Routes>
                <Route path="/map" element={<Map/>}/>
                <Route path="/storyList" element={<StoryList/>}/>
                <Route path="/story" element={<Story/>}/>
                <Route path="/calendar" element={<Calendar/>}/>
                <Route path="/settings" element={<Settings/>}/>
            </Routes>
        </div>
    );
};

export default MainContent;
