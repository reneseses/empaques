$(function(){
	$.get(appContext + "member/planillas/ajax/getall", function(data){
		$("#loading-principal").hide();
		$("#table-panel").show();

		var options= [];

		for(var i=0; i< data.length; i++){
			var obj= {
				value	: data[i].fecha,
				text	: data[i].fecha
			}

			options.push(obj);
		}

		$("#planilla_select").selectize({
			options: options
		}).on("change", function(){
			var value= $(this).val();

			$("#table-list tbody").empty();
			$("#table-list").hide();
			$(".empty").hide();
			$("#loading-inner").show();
			$("#planilla_select")[0].selectize.disable();

			$.get(listUrl, {week: value}, function(sols){

				if(sols.length==0){
					$("#planilla_select")[0].selectize.enable();
					$("#loading-inner").hide();
					$(".empty").show();
					return;
				}

				for(var i=0; i < sols.length; i++){
					var sol	= sols[i],
						tr	= $("<tr>");

					var num			= $("<td class='text-right' >").text(sol.num),
						empaque		= $("<td class='text-right' >").text(sol.empaque),
						fecha		= $("<td class='text-right' >").text(sol.fecha),
						solicitudes	= $("<td>");

					for(j=1; j <= 5; j++){
						if(sol["turno" + j])
							solicitudes.append("<div>" + sol["turno" + j] + "</div>");
					}
					console.log(solicitudes);
					tr.append(num)
						.append(empaque)
						.append(fecha)
						.append(solicitudes)

					$("#table-list tbody").append(tr);
				}
				$("#loading-inner").hide();
				$("#table-list").show();
				$("#planilla_select")[0].selectize.enable();
			}).fail(function(e){
				console.log(e);
			})
		});

		if(data.length > 0)
			$("#planilla_select")[0].selectize.setValue(data[0].fecha);

	}).fail(function(e){
		console.log(e)
	});
})