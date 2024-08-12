import "../../css/card.scss"
import {useLocation, useNavigate} from "react-router-dom";
import axios from "axios";

axios.defaults.withCredentials = true;

export const Card = ({story_id, title, content, img_url, place, date}) => {

    const navigate = useNavigate();
    const location = useLocation();
    const currentParams = new URLSearchParams(location.search);

    return (
        <div className="card-wrapper" onClick={() => {
            navigate(`/main/story/${story_id}?${currentParams.toString()}`);
        }}>
            <div className="card-body-img">
                <img src={img_url}/>
            </div>
            <div className="card-body-text">
                <div className="card-body-text-title">{title}</div>
                <div className="card-body-text-content">{content}</div>
            </div>

            <div className="card-footer">
                <div className="place">{place}</div>
                <div className="date">{date}</div>
            </div>
        </div>
    );
};