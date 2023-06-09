import $ from "jquery"
import { useContext, useEffect, useRef, useState } from "react";
import { DataContext } from "./dataContext";
import { useNavigate } from "react-router-dom";

export default function Login(){

	const navigate = useNavigate()
	const email = useRef(null)
	const password = useRef(null)
	const [isRegister, setIsRegister] = useState(false);
	const register = useRef(null)
	const login = useRef(null)
	const loginButton = useRef(null)
	const {setIsLoggedIn} = useContext(DataContext)

	const [loginVisibility, setLoginVisibility] = useState(false)
	const [registerVisibility, setRegisterVisibility] = useState(true)

	const {setUsername, setHistory, history} = useContext(DataContext)

    function handleSubmit(event){
        event.preventDefault(); // 👈️ prevent page refresh
		let emailValue = email.current.value
		let passwordValue = password.current.value
		console.log(emailValue, passwordValue)
		if (isRegister){
			$.post("http://localhost:8080/register",{username:emailValue, password:passwordValue}, (success)=>{

				if (success){
					changeRegister()
					alert(`Register Successfully, Welcome ${emailValue}!`)
				}
				else{
					alert(`The username has been registered, please choose another one.`)
				}
			})
		}
		else {
			$.post("http://localhost:8080/login",{username:emailValue, password:passwordValue}, (data)=>{
				const success = data["success"]
				const userHistory = data["history"]
				if (success){
					setUsername(emailValue)
					setHistory(userHistory)
					setIsLoggedIn(true)
					navigate("/")
					alert(`Login Successfully, Welcome ${emailValue}!`)
				}
				else{
					alert(`The username or password is not correct.`)
				}
			})
		}
        return false;
    }

	function backToMain(){
		navigate("/")
	}


	function changeRegister(){
		console.log(isRegister)
		if (isRegister){
			console.log(1)
			setIsRegister(false)
			let button = document.getElementById("loginButton")
			button.textContent = "Login"
			setLoginVisibility(false)
			setRegisterVisibility(true)
		}
		else {
			console.log(2)
			setIsRegister(true)
			console.log(isRegister)
			let button = document.getElementById("loginButton")
			button.textContent = "Register"
			setLoginVisibility(true)
			setRegisterVisibility(false)
		}
	}

	useEffect(() => {
		/*==================================================================
    [ Focus input ]*/
    $('.input100').each(function(){
        $(this).on('blur', function(){
            if($(this).val().trim() != "") {
                $(this).addClass('has-val');
            }
            else {
                $(this).removeClass('has-val');
            }
        })    
    })
  
    /*==================================================================
    [ Show pass ]*/
    var showPass = 0;
    $('.btn-show-pass').on('click', function(){
        if(showPass == 0) {
            $(this).next('input').attr('type','text');
            $(this).find('i').removeClass('zmdi-eye');
            $(this).find('i').addClass('zmdi-eye-off');
            showPass = 1;
        }
        else {
            $(this).next('input').attr('type','password');
            $(this).find('i').addClass('zmdi-eye');
            $(this).find('i').removeClass('zmdi-eye-off');
            showPass = 0;
        }
        
    });
	})
	
    return (
        <div>
            <div className="limiter">
		<div className="container-login100">
			<div className="wrap-login100">

				<form className="login100-form validate-form" onSubmit={handleSubmit}>
					<span className="login100-form-title p-b-26">
						Welcome
					</span>
					<span className="login100-form-title p-b-48">
						<i className="zmdi zmdi-font"></i>
					</span>

					<div className="wrap-input100 validate-input" data-validate = "Valid email is: a@b.c">
						<input className="input100" type="text" name="email" ref={email}></input>
						<span className="focus-input100" data-placeholder="Username"></span>
					</div>

					<div className="wrap-input100 validate-input" data-validate="Enter password">
						<span className="btn-show-pass">
							<i className="zmdi zmdi-eye"></i>
						</span>
						<input className="input100" type="password" name="pass" ref={password}></input>
						<span className="focus-input100" data-placeholder="Password"></span>
					</div>

					<div className="container-login100-form-btn">
						<div className="wrap-login100-form-btn">
							<div className="login100-form-bgbtn"></div>
							<button className="login100-form-btn" ref={loginButton} id="loginButton">
								Login
							</button>
						</div>
					</div>

					<div className="text-center">
						<button className="button-30" onClick={backToMain} style={
							{
								marginTop:"40px",
							}
						}>Back to main page</button>
					</div>



					<div className="text-center p-t-115" style={{display:registerVisibility?"block":"none"}} id="register">
						<span className="txt1">
							Don't have an account?
						</span>

						<a className="txt2" onClick={changeRegister}>
							Sign Up
						</a>
					</div>
					<div className="text-center p-t-115" style={{display:loginVisibility?"block":"none"}} id="login">
						<span className="txt1">
							Already have an account?
						</span>

						<a className="txt2" onClick={changeRegister}>
							Login
						</a>
					</div>
				</form>
			</div>
		</div>
	</div>
        </div>
    )
}