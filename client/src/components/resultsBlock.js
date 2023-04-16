import { useState } from "react";
import ExpandableList from "./expandableList";

export default function ResultBlock({info}){
    return (
        <div className="block">
            <div className="title"><a href={info.url}>Title: {info.Title}</a></div>
            <div className="url"><a href={info.url}>URL: {info.url}</a></div>
            <div className="moddate">Last Modification Date: {info["Last-Modified"]}</div>
            <div className="pagesize">Size of Page: {info["Content-Length"]}</div>
            <div className="parentlink">Parent link: 
                <ExpandableList items={info["parentlinks"]}></ExpandableList>
            </div>
            <div className="sublink">Child link: 
                <ExpandableList items={info["sublinks"]}></ExpandableList>
            </div>
        </div>
    )
}