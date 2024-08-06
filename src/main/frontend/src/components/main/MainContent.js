import React from 'react';
import {Route, Routes} from 'react-router-dom';
import Map from './Map';
import Settings from './Settings';
import Calendar from './Calendar';
import Write from "./Write";
import StoryList from "./StoryList"
import Story from "./Story";
import EditStory from "./EditStory";

const MainContent = () => {

    return (
        <div className="main-content">
            <Routes>
                <Route path="/map" element={<Map/>}/>
                <Route path="/story/:story_id" element={<Story/>}/>
                <Route path="/edit-story/:story_id" element={<EditStory/>}/>
                <Route path="/storyList" element={<StoryList/>}/>
                <Route path="/write" element={<Write/>}/>
                <Route path="/calendar" element={<Calendar/>}/>
                <Route path="/settings" element={<Settings/>}/>
            </Routes>
        </div>
    );
};

export default MainContent;
