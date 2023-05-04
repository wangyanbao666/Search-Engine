import { useContext, useEffect, useState } from "react"
import { DataContext } from "./dataContext"
import { useNavigate } from "react-router-dom"
import Login from "./login"
import ResultBlock from "./resultsBlock"

export default function SearchHistory(){
    const {isLoggedIn} = useContext(DataContext)
    const {history} = useContext(DataContext)
    const [curResult, setCurResult] = useState([])
    const navigate = useNavigate()
    const [keywords, setKeywords] = useState(Object.keys(history))

    useEffect(() => {
        console.log(isLoggedIn)
        if (!isLoggedIn){
            navigate("/login")
        }

    })

    function updateDisplay(keyword){
        const results = history[keyword]
        setCurResult(results)
    }

    function backToMain(){
		navigate("/")
	}

    let key=1;

    return (
        <div>
            <div className="text-center">
						<button className="button-30" onClick={backToMain} style={
							{
								marginTop:"40px",
							}
						}>Back to main page</button>
					</div>
            <div className="container">
            <div className="sidebar">
                <ul className="sidebar-list">
                {keywords.slice(0,20).map((keyword) => <li><a onClick={() => {updateDisplay(keyword)}}>{keyword}</a></li>)}
                </ul>
            </div>
            </div>

            <div className="container">
                {[...curResult].map(result => <ResultBlock info={result} key={key++}></ResultBlock>)}
            </div>
        </div>
    )
}