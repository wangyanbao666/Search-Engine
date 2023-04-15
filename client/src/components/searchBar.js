import $ from "jquery"

function handleSubmit(event){
    event.preventDefault(); // 👈️ prevent page refresh
    let searchBar = document.getElementById("search")
    let query = searchBar.value
    console.log(query)
    $.post("http://localhost:8080/api/results", {query: query}, function(data){
        const info = {
            title: "ttt",
            url: "url"
        }

    })
    // window.location.replace("/result")
    return false;
}


export default function SearchBar(){
    return (
        <div>
            <form className="form-wrapper" onSubmit={handleSubmit}>
                <input type="text" id="search" placeholder="Search for..." required></input>
                <input type="submit" value="go" id="submit"></input>
            </form>
        </div>
    )
}