import $ from "jquery"
import { DataContext } from "./dataContext";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";


export default function SearchBar({ className }){
    const { setData,username,setHistory,history } = useContext(DataContext);
    const navigate = useNavigate();

    function handleSubmit(event){
        event.preventDefault(); // 👈️ prevent page refresh
        let searchBar = document.getElementById("search")
        let query = searchBar.value
        // console.log(query)
        $.post("http://localhost:8080/api/results", {query: query, username: username}, function(data){
            setData(data)
            console.log(history)
            history[query] = data
            setHistory(history)
            navigate("/result")
        })
        return false;
    }

    function viewHistory(){
        navigate("/history")
    }
    
    return (
        <div>
            <form className="form-wrapper" onSubmit={handleSubmit}>
                <input type="text" id="search" placeholder="Search for..." required></input>
                <input type="submit" value="go" id="submit"></input>
            </form>
            <button onClick={viewHistory} className="button-30">View Search History</button>
        </div>
    )
}