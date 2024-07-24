import React, { useState, useEffect } from "react";
import axios from "axios";
import { Button, TextField, List, ListItem, ListItemText } from "@mui/material";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import '../../css/Story.css';

axios.defaults.withCredentials = true;

const Story = () => {
    const [title, setTitle] = useState("");
    const [date, setDate] = useState(new Date());
    const [location, setLocation] = useState(null);
    const [content, setContent] = useState("");
    const [image, setImage] = useState({ image_file: null, preview_URL: '/images/anonymous.png' });
    const [searchResults, setSearchResults] = useState([]);
    const accessToken = localStorage.getItem('accessToken');

    /**
     * 네이버 검색 Api Call
     */
    const handleSearch = async (text) => {
        if (text && typeof text === 'string' && text.trim() !== "") {
            try {
                const response = await axios.get("http://localhost:8080/naver/search", {
                    params: { text },
                    headers: {
                        "X-Naver-Client-Id": process.env.REACT_APP_NAVER_CLIENT_ID,
                        "X-Naver-Client-Secret": process.env.REACT_APP_NAVER_CLIENT_SECRET,
                        Authorization: `Bearer ${accessToken}`
                    }
                });
                console.log("Search results:", response.data.items); // Debugging line
                setSearchResults(response.data.items);
            } catch (error) {
                console.error("Error fetching data: ", error);
                setSearchResults([]);
            }
        } else {
            setSearchResults([]);
        }
    };

    /**
     * 이미지 처리
     */
    const handleImageChange = (e) => {
        e.preventDefault();
        const fileReader = new FileReader();
        if (e.target.files[0]) {
            fileReader.readAsDataURL(e.target.files[0]);
            fileReader.onload = () => {
                setImage({ image_file: e.target.files[0], preview_URL: fileReader.result });
            };
        }
    };

    /**
     * 검색 결과 선택
     */
    const handleResultClick = (item) => {
        if (item) {
            setLocation(item);
        }
        setSearchResults([]);
    };

    /**
     * 일기 작성 버튼 클릭
     */
    const handleSubmit = async () => {
        if (location) {
            try {
                await axios.post("/your/api/endpoint", {
                    title,
                    date,
                    location: {
                        title: location.title.replace(/<\/?b>/g, ""),
                        address: location.address,
                        mapx: location.mapx,
                        mapy: location.mapy
                    },
                    content,
                    image: image.image_file
                });
                setTitle("");
                setDate(new Date());
                setLocation(null);
                setContent("");
                setImage({ image_file: null, preview_URL: null });
            } catch (error) {
                console.error("Error submitting form: ", error);
            }
        } else {
            console.error("No location selected");
        }
    };


    useEffect(() => {
        if (location && typeof location === 'string' && location.trim() === "") {
            setSearchResults([]);
        }
    }, [location]);


    return (
        <div className="diary-entry-page">
            <div className="top-section">
                <div className="form-left">
                    <TextField
                        label="제목"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        fullWidth
                    />
                    <Calendar
                        value={date}
                        onChange={setDate}
                    />
                </div>
                <div className="form-right">
                    <div className="image-upload">
                        <input
                            type="file"
                            accept="image/*"
                            onChange={handleImageChange}
                            style={{display: "none"}}
                            id="image-input"
                        />
                        <Button
                            variant="outlined"
                            color="primary"
                            onClick={() => document.getElementById("image-input").click()}
                        >
                            😎사진 고르기😎
                        </Button>
                        <div className="img-wrapper">
                            {image.preview_URL && (
                                <img
                                    src={image.preview_URL}
                                    alt="Preview"
                                    className="preview-image"
                                />
                            )}
                        </div>
                    </div>
                </div>
            </div>
            <div className="form-center">
                <div className="location-search">
                    <TextField
                        label="지역"
                        value={location}
                        onChange={(e) => {
                            setLocation(e.target.value);
                            handleSearch(e.target.value);
                        }}
                        fullWidth
                    />
                    {searchResults.length > 0 && (
                        <div id="resultContainer">
                            {searchResults && searchResults.length > 0 ? (
                                <List>
                                    {searchResults.map((item, index) => (
                                        <ListItem button key={index} onClick={() => handleResultClick(item)}>
                                            <ListItemText
                                                primary={item.title.replace(/<\/?b>/g, "")}
                                                secondary={item.address}
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                            ) : (
                                <p>No results found</p>
                            )}
                        </div>

                    )}
                </div>
                <TextField
                    label="내용"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    multiline
                    rows={4}
                    fullWidth
                />
            </div>
            <div className="form-bottom">
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSubmit}
                >
                저장하기
                </Button>
            </div>
        </div>
    );
};

export default Story;
