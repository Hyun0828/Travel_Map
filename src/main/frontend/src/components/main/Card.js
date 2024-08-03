import "../../css/card.scss"
import {useNavigate} from "react-router-dom";

export const Card = ({story_id, title, content, img_url, place, date}) => {
    const navigate = useNavigate();
    return (
        <div className="card-wrapper" onClick={() => {
            navigate(`/story/${story_id}`)
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