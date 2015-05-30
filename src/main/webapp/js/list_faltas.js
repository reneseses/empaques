$(function(){
	$.get(appContext + "member/planillas/ajax/getall", function(data){
		$("#loading-principal").hide();
		$("#table-panel").show();

		var options= [];

		for(var i=0; i< data.length; i++){
			var obj= {
				value	: data[i].id,
				text	: data[i].fecha
			}

			options.push(obj);
		}

		$("#planilla_select").selectize({
			options: options
		}).on("change", function(){
			var value= $(this).val();

			$(".empty").hide();
			$("#table-list tbody").empty();
			$("#table-list").hide();
			$("#loading-inner").show();
			$("#planilla_select")[0].selectize.disable();

			$.get(appContext + "member/faltas", {planilla: value}, function(faltas){
				if(faltas.length == 0){
					$("#loading-inner").hide();
					$("#planilla_select")[0].selectize.enable();
					$(".empty").show();
					return;
				}
				for(var i=0; i < faltas.length; i++){
					var falta	= faltas[i],
						tr		= $("<tr>");

					var num		= $("<td class='text-right'>").text(i + 1),
						empnum	= $("<td class='text-right'>").text(falta.usuario.numero),
						empname	= $("<td>").text(falta.usuario.nombre),
						type	= $("<td>").text(falta.tipo);

					tr.append(num)
						.append(empnum)
						.append(empname)
						.append(type);


					if(isAdmin){
						var update= $('<td class="utilbox">' +
							'<a title="Actualizar Planilla" alt="Actualizar Planilla" class="btn btn-warning btn-xs" href="' + appContext + 'member/faltas/' + falta.id + '?form">' +
								'<i class="fa fa-edit"></i>' +
							'</a></td>');

						var remove	= $('<td class="utilbox">'+
							'<form id="command" action="' + appContext + 'member/faltas/' + falta.id + '" method="post">' +
								'<input type="hidden" name="_method" value="DELETE">' +
								'<button onclick="return confirm(\'Estas seguro que quieres eliminar este elemento\');" title="Borrar Falta" class="btn btn-danger btn-xs">' +
									'<i class="fa fa-times"></i>' +
								'</button>'+
							'</form></td>');
						
						tr.append(update)
							.append(remove);
					}

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
			$("#planilla_select")[0].selectize.setValue(data[0].id);

	}).fail(function(e){
		console.log(e)
	});
})