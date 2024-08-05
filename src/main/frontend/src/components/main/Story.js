import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Dialog, DialogContent, IconButton } from "@mui/material";
import BuildOutlinedIcon from '@mui/icons-material/BuildOutlined';
import DeleteForeverOutlinedIcon from '@mui/icons-material/DeleteForeverOutlined';
import DisabledByDefaultOutlinedIcon from '@mui/icons-material/DisabledByDefaultOutlined';
import axios from "axios";
import "../../css/Story.scss";

axios.defaults.withCredentials = true;

const Story = () => {

    const { story_id } = useParams();
    const [story, setStory] = useState({});
    const [images, setImages] = useState([]);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [isLoaded, setIsLoaded] = useState(false);
    const navigate = useNavigate();
    const accessToken = localStorage.getItem('accessToken');
    const [show, setShow] = useState(false);

    useEffect(() => {
        const getStory = async () => {
            const response = await axios.get(`http://localhost:8080/story?storyId=${story_id}`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                }
            });
            return response.data;
        };

        const getImages = async () => {
            const response = await axios.get(`http://localhost:8080/storyImages?storyId=${story_id}`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                }
            });
            const imageUrls = response.data;

            console.log(imageUrls);

            const imagePromises = imageUrls.map(async (imageUrl) => {
                const response = await fetch(imageUrl);
                const blob = await response.blob();
                return URL.createObjectURL(blob);
            });

            return Promise.all(imagePromises);
        };

        getStory().then(result => setStory(result)).then(() => setIsLoaded(true));
        getImages().then(imageUrls => setImages(imageUrls));
    }, [story_id, accessToken]);

    const handleNextImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex + 1) % images.length);
    };

    const handlePrevImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex - 1 + images.length) % images.length);
    };

    return (
        <React.Fragment>
            <div className="story-wrapper">
                <div className="edit-delete-button">
                    <Button
                        variant="outlined" color="error" endIcon={<DeleteForeverOutlinedIcon />}
                        className="delete-button"
                        onClick={() => {
                            setShow(true);
                        }}
                    >
                        삭제
                    </Button>
                    <Button
                        variant="outlined" endIcon={<BuildOutlinedIcon />}
                        onClick={() => {
                            navigate(`/edit-story/${story_id}`);
                        }}
                    >
                        수정
                    </Button>
                </div>
                <div className="story-header">
                    <div className="story-header-place">{story.place}</div>
                    <div className="story-header-date">{story.date}</div>
                </div>
                <hr />
                <div className="story-body">
                    <div className="story-image">
                        {images.length > 0 && (
                            <div className="image-container">
                                <button onClick={handlePrevImage}>&lt;</button>
                                <img src={images[currentImageIndex]} alt={`Story image ${currentImageIndex + 1}`} />
                                <button onClick={handleNextImage}>&gt;</button>
                            </div>
                        )}
                    </div>
                    <div className="story-title-content">
                        <div className="story-title">{story.title}</div>
                        <div className="story-content">{story.content}</div>
                    </div>
                </div>
                <hr />
                <div className="story-footer"></div>
            </div>
            <Dialog open={show}>
                <DialogContent style={{ position: "relative" }}>
                    <IconButton
                        style={{ position: "absolute", top: "0", right: "0" }}
                        onClick={() => setShow(false)}
                    >
                        <DisabledByDefaultOutlinedIcon />
                    </IconButton>
                    <div className="modal">
                        <div className="modal-title"> 정말 삭제하시겠습니까 ?</div>
                        <div className="modal-button">
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={async () => {
                                    setShow(false);
                                }}
                            >
                                예
                            </Button>
                            <Button
                                variant="outlined"
                                color="primary"
                                onClick={() => {
                                    setShow(false);
                                }}
                            >
                                아니오
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </React.Fragment>
    );
};

export default Story;
