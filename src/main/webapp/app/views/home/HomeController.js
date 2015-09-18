app.controller('HomeController', ["$scope", function($scope){
    var refreshLinks= function(){
        $(".thumbnail.tail").each(function(){
            var	width	= $(this).width(),
                height	= width > 200? 200: width - 10,
                text	= $(this).find("h3"),
                tHeight = text.height();

            $(this).height(height);
            height= $(this).height();
            text.css({bottom: height /2 - 5});
        });
    }

    refreshLinks();

    $(".thumbnail.tail").fadeIn().css("display","block");;

    $(window).resize(function(){
        refreshLinks();
    })
}]);
