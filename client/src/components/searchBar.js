import $ from "jquery"
import { DataContext } from "./dataContext";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";


export default function SearchBar({ className }){
    const { setData } = useContext(DataContext);
    const navigate = useNavigate();

    function handleSubmit(event){
        event.preventDefault(); // 👈️ prevent page refresh
        let searchBar = document.getElementById("search")
        let query = searchBar.value
        // console.log(query)
        $.post("http://localhost:8080/api/results", {query: query}, function(data){
            const info = [
                {
                    title: "ttt",
                    url: "url"
                }, 
                {
                    title: "hkust",
                    url: "hkust url"
                },
                {
                    title: query,
                    url: "none"
                }
            ]
            setData(data)
            navigate("/result")
        })
        return false;
    }
    
    return (
        <div>
            <form className="form-wrapper" onSubmit={handleSubmit}>
                <input type="text" id="search" placeholder="Search for..." required></input>
                <input type="submit" value="go" id="submit"></input>
            </form>
        </div>
    )
}