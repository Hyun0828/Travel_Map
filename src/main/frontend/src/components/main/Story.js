import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {Button, Dialog, DialogContent, IconButton} from "@mui/material";
import BuildOutlinedIcon from '@mui/icons-material/BuildOutlined';
import DeleteForeverOutlinedIcon from '@mui/icons-material/DeleteForeverOutlined';
import DisabledByDefaultOutlinedIcon from '@mui/icons-material/DisabledByDefaultOutlined';
import axios from "axios";
import "../../css/Story.scss";
import instance from "../main/axios/TokenInterceptor";


const Story = () => {

    const {story_id} = useParams();
    const [story, setStory] = useState({});
    const [images, setImages] = useState([]);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [isLoaded, setIsLoaded] = useState(false);
    const navigate = useNavigate();
    // modal 창이 보이는가?
    const [show, setShow] = useState(false);

    useEffect(() => {
        const getStory = async () => {
            // const response = await axios.get(`http://localhost:8080/story?storyId=${story_id}`, {
            //     headers: {
            //         'Authorization': `Bearer ${accessToken}`
            //     }
            // });
            const response = await instance.get(`http://localhost:8080/story?storyId=${story_id}`);
            return response.data;
        };

        const getImagesUrl = async () => {
            // const response = await axios.get(`http://localhost:8080/storyImages?storyId=${story_id}`, {
            //     headers: {
            //         'Authorization': `Bearer ${accessToken}`
            //     }
            // });
            const response = await instance.get(`http://localhost:8080/storyImages?storyId=${story_id}`);
            const imageUrls = await Promise.all(
                response.data.map(async (image) => {
                    const imgResponse = await instance.get(`http://localhost:8080${image}`, {
                        responseType: 'blob'
                    });
                    return URL.createObjectURL(imgResponse.data);
                })
            );
            setImages(imageUrls);
        };

        getStory().then(result => setStory(result)).then(() => setIsLoaded(true));
        getImagesUrl();
    }, [story_id]);

    const handleNextImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex + 1) % images.length);
    };

    const handlePrevImage = () => {
        setCurrentImageIndex((prevIndex) => (prevIndex - 1 + images.length) % images.length);
    };

    const deleteStory = async () => {
        // await axios.delete(`http://localhost:8080/story?storyId=${story_id}`, {
        //     headers : {
        //         'Authorization' : `Bearer ${accessToken}`
        //     }
        // });
        await instance.delete(`http://localhost:8080/story?storyId=${story_id}`);
    }

    return (
        <React.Fragment>
            <div className="story-wrapper">
                <div className="edit-delete-button">
                    <Button
                        variant="outlined" color="error" endIcon={<DeleteForeverOutlinedIcon/>}
                        className="delete-button"
                        onClick={() => {
                            setShow(true);
                        }}
                    >
                        삭제
                    </Button>
                    <Button
                        variant="outlined" endIcon={<BuildOutlinedIcon/>}
                        onClick={() => {
                            navigate(`/main/edit-story/${story_id}`);
                        }}
                    >
                        수정
                    </Button>
                </div>
                <div className="story-header">
                    <div className="story-header-place">{story.place}</div>
                    <div className="story-header-date">{story.date}</div>
                </div>
                <hr/>
                <div className="story-body">
                    <div className="story-image">
                        {images.length > 0 && (
                            <div className="image-carousel">
                                <img src={images[currentImageIndex]}
                                     alt={`Story image ${currentImageIndex + 1}`} />
                                <div className="image-navigation">
                                    <Button className="img_button" onClick={handlePrevImage}>&lt;</Button>
                                    <Button className="img_button" onClick={handleNextImage}>&gt;</Button>
                                </div>
                            </div>
                        )}
                    </div>
                    <div className="story-title-content">
                        <div className="story-title">{story.title}</div>
                        <div className="story-content">{story.content}</div>
                    </div>
                </div>
                <hr/>
                <div className="story-footer"></div>
            </div>
            <Dialog open={show}>
                <DialogContent style={{position: "relative"}}>
                    <IconButton
                        style={{position: "absolute", top: "0", right: "0"}}
                        onClick={() => setShow(false)}
                    >
                        <DisabledByDefaultOutlinedIcon/>
                    </IconButton>
                    <div className="modal">
                        <div className="modal-title"> 정말 삭제하시겠습니까 ?</div>
                        <div className="modal-button">
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={async () => {
                                    setShow(false);
                                    await deleteStory();
                                    alert("게시물이 삭제되었습니다😎");
                                    window.location.href = "/main/storyList?page=1";
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
