import ExpandableList from "./expandableList";
import { useNavigate } from "react-router-dom";
import $ from "jquery"
import { DataContext } from "./dataContext";
import { useContext } from "react";

export default function ResultBlock({info}){
    let keywords = info.keywords;
    let key = 1;
    const navigate = useNavigate();
    const { setData } = useContext(DataContext);
    function request(query){
        console.log(query)
        $.post("http://localhost:8080/api/results", {query: query}, function(data){
            setData(data)
            navigate("/result")
        })
    }
    return (
        <div className="block">
            <div className="title"><a href={info.url}>Title: {info.Title}</a></div>
            <div className="url"><a href={info.url}>URL: {info.url}</a></div>
            <div className="moddate">Last Modification Date: {info["Last-Modified"]}</div>
            <div className="pagesize">Size of Page: {info["Content-Length"]}</div>
            <div className="keywords">Keywords: {"\u00A0"}
                {keywords.map((text) => <span><a onClick={() => request(text)}>{text}</a>{"\u00A0"}{"\u00A0"}{"\u00A0"}{"\u00A0"}</span>)}
            </div>
            <div className="parentlink">Parent link: 
                <ExpandableList items={info["parentlinks"]} key={key++}></ExpandableList>
            </div>
            <div className="sublink">Child link: 
                <ExpandableList items={info["sublinks"]} key={key++}></ExpandableList>
            </div>
        </div>
    )
}