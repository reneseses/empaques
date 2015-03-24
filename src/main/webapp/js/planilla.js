var changes= [];
var grid;

function negativeValueRenderer(instance, td, row, col, prop, value, cellProperties) {
	Handsontable.TextCell.renderer.apply(this, arguments);
	$(td).css({
		background: '#fff',
		width: 105,
		textAlign: "right"
	});
	
	if(value === '') {
		td.style.background = '#eee';
	}
	else if(value === '-') {
		td.style.background = '#c1e2b3';
		td.style.color= "#468847";
		td.style.textAlign = "center";
	}
	else if(value === "  "){
		td.style.background = '#f0f0f0';
		td.style.textAlign = "center";
	}
}
var columns;

if(readonly){
	columns = [
		{data: "Lunes", readOnly: true},
		{data: "Martes", readOnly: true},
		{data: "Miercoles", readOnly: true},
		{data: "Jueves", readOnly: true},
		{data: "Viernes", readOnly: true},
		{data: "Sabado", readOnly: true},
		{data: "Domingo", readOnly: true},
	];
}

else{
	columns = [
		{data: "Lunes"},
		{data: "Martes"},
		{data: "Miercoles"},
		{data: "Jueves"},
		{data: "Viernes"},
		{data: "Sabado"},
		{data: "Domingo"},
	];
}

$(function () {
	var startDate= date;
	var endDate= date;
	
	var curr_date = date.getDate(); if(curr_date <10) curr_date= "0" + curr_date;
	var curr_month = date.getMonth() + 1; if(curr_month <10) curr_month= "0" + curr_month;
	var curr_year = date.getFullYear();
	
	//$('.week-picker').val(curr_date + "-" + curr_month + "-" + curr_year);

	var height= $(window).height() - $("#planilla").position().top - 1;
	var width= $("#planilla").width() - 65;

	$("#planilla").height(height - 20);

	var colWidth= parseInt(width/7);

	colWidth= colWidth>80? colWidth: 80;

	var lastWidth= width - colWidth* 6;

	lastWidth= lastWidth> 80? lastWidth: 80;

	$.ajax({
        url: dataUrl,
        dataType: 'json',
        success: function(data) {
        	var first		= true,
        		container	= document.getElementById('planilla');

        	grid= new Handsontable(container, {
        		colWidths	: [colWidth, colWidth, colWidth, colWidth, colWidth, colWidth, lastWidth],
        		data		: data[1],
        		colHeaders	: ["Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"],
        		rowHeaders	: data[0],
        		columns		: columns,
        		fillHandle	: fill,
        		maxRows		: data.length,
        		cells		: function (row, col, prop) {
					var cellProperties= {};
					if(this.instance.getData()[row][prop] == '')
						cellProperties.readOnly=true;
					
					cellProperties.renderer= negativeValueRenderer;

					return cellProperties;
				}
        	});

        	grid.addHook('afterChange', function(change){
        		getChanges(change, grid.getData());
        	})

        	$(".handsontable table").addClass("table table-bordered table-striped table-hover");
        }
    });
	

	var selectCurrentWeek = function() {
	    window.setTimeout(function () {
	        $('.xdsoft_datetimepicker').find('.xdsoft_current').parent().addClass('xdsoft_row_current');
	    }, 40);
	};

	$('.week-picker').keydown(function(e){
		e.preventDefault();
		return;
	}).datetimepicker({
		format      	: 'Y-m-d 00:00',
		timepicker		: false,
		dayOfWeekStart	: 1,
		format			: 'd-m-Y',
		value			: moment(date).format("DD-MM-YYYY"),
		onShow:function( ct ){
			selectCurrentWeek();
		},
		onSelectDate: function(ct){
			var mom= moment(ct).startOf("week").add(1, "d");
			var d= mom.toDate();
			console.log(d);
			this.setOptions({
				value: moment(d).format("DD-MM-YYYY")
			});

			selectCurrentWeek();
		},
	});

	$('body').on('mouseover', '.xdsoft_datetimepicker .xdsoft_calendar tbody tr', function() { $(this).addClass('xdsoft_row_hover'); });
	$('body').on('mouseout','.xdsoft_datetimepicker .xdsoft_calendar tbody tr', function() { $(this).removeClass('xdsoft_row_hover'); });
});

function save(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(grid.getData()), fecha: $(".week-picker").val()},
		type: "POST",
		success:function(){
			window.location.href = parent + "member/planillas?page=1&size=10";
		}
	});
}

function updateTurnos(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(grid.getData()), id: planillaId},
		type: "POST",
		success:function(){
			window.location.href = parent + "member/planillas?page=1&size=10";
		}
	});
}

function update(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(changes), id: planillaId},
		type: "POST",
		success:function(){
			window.location.href = parent + "member/planillas?page=1&size=10";
		}
	});
}
