import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Map from './Map';
import Settings from './Settings';
import Calendar from './Calendar';

const MainContent = () => {
    return (
        <div className="main-content">
            <Routes>
                <Route path="/map" element={<Map />} />
                <Route path="/calendar" element={<Calendar />} />
                <Route path="/settings" element={<Settings />} />
            </Routes>
        </div>
    );
};

export default MainContent;
