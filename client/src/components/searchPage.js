import SearchBar from "./searchBar"

export default function SearchPage({className}){
    return (
        <div className="App">
        <header></header>
        <div className="contain">
          <div className={className}> Search Engine </div>
          <SearchBar></SearchBar>
        </div>
      </div>
    )
}