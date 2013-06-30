$(function(){
	
	loadingIndicator = $('<span class="loading-indicator"><label>Loading...</label></span>');
	$("body").append(loadingIndicator);
    loadingIndicator.css("position", "absolute");	
  	loadingIndicator.css("top", $("#main").position().top + 200);
  	loadingIndicator.css("left", $("#main").position().left + $("#main").width() / 2 - loadingIndicator.width() / 2);
    loadingIndicator.hide();
    
    loadingIndicator.ajaxStart(function(){
    	$(this).show();
    });
    loadingIndicator.ajaxStop(function(){
    	$(this).fadeOut();
    });
	
	$(".header_user_img").click(function(event){
		event.preventDefault();
		event.stopPropagation();
		if($("#user_menu").css("display") == "none")
			$("#user_menu").effect("blind", { mode : "show", direction : "vertical" }, 200);
		else
			$("#user_menu").effect("blind", { mode : "hide", direction : "vertical" }, 200);
	});
	
	$("html").click(function(){
		$("#user_menu").effect("blind", { mode : "hide", direction : "vertical" }, 200);
	});
});