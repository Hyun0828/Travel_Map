const CustomMapMarker = ({title, windowWidth}) => {
    const mobileContentArray = [
        '<div style="margin: 0; display: table; padding: 0.5rem; table-layout: auto; border-radius: 2.3rem; border: 0.2rem solid var(#006400); background: white; cursor: pointer; position: relative; z-index: 2">',
        '<div style="display: table-cell; display: inline-block; width: 2.5rem; height: 2.5rem; background-image: url(/images/markerIcon.png); background-size: cover; background-position: center; background-repeat: no-repeat;"></div>',
        '<span style="position: absolute; border-style: solid; border-width: 1rem 1rem 0 1rem; border-color: #ffffff transparent; display: block; width: 0; z-index: 1; top: 3.1rem; left: 0.75rem;"></span>',
        '<span style="position: absolute; border-style: solid; border-width: 1rem 1rem 0 1rem; border-color: #006400 transparent; display: block; width: 0; z-index: 0; top: 3.35rem; left: 0.75rem;"></span>',
        '</div>',
    ];

    const PCContentArray = [
        '<div style="margin: 0; display: table; padding: 0.5rem; table-layout: auto; border-radius: 2.3rem; border: 0.2rem solid var(#006400); background: white; cursor: pointer; position: relative; z-index: 2">',
        '<div style="display: table-cell; display: inline-block; width: 4rem; height: 4rem; background-image: url(/images/markerIcon.png); background-size: cover; background-position: center; background-repeat: no-repeat;"></div>',
        '<div style="max-width: 23rem; height: 4rem; padding: 0 0.8rem 0 0.8rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: table-cell; vertical-align: middle; cursor: pointer; font-size: 1.5rem; letter-spacing: -0.04rem; font-weight: 600; line-height: 4rem;">',
        title,
        '</div>',
        '<span style="position: absolute; border-style: solid; border-width: 1.2rem 1rem 0 1rem; border-color: #ffffff transparent; display: block; width: 0; z-index: 1; top: 4.8rem; left: 1.4rem;"></span>',
        '<span style="position: absolute; border-style: solid; border-width: 1.2rem 1rem 0 1rem; border-color: #006400 transparent; display: block; width: 0; z-index: 0; top: 5.05rem; left: 1.4rem;"></span>',
        '</div>',
    ];
    // 화면 크기에 따라 모바일용, pc용 마커 반환
    return windowWidth < 768 ? mobileContentArray.join('') : PCContentArray.join('');
};

export default CustomMapMarker;