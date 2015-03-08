$(function(){
		
	$(".header_user_img").click(function(event){
		event.preventDefault();
		event.stopPropagation();
		if($("#user_menu").css("display") == "none")
			$("#user_menu").effect("blind", { mode : "show", direction : "vertical" }, 200);
		else
			$("#user_menu").effect("blind", { mode : "hide", direction : "vertical" }, 200);
	});
	
});