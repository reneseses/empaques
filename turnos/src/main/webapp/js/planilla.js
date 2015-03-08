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
		td.style.background = '#f0f0f0';
	}
	else if(value === '-') {
		td.style.background = '#99ff99';
		td.style.textAlign = "center";
	}
	else if(value === "  "){
		td.style.background = '#f0f0f0';
		td.style.textAlign = "center";
	}
}

var defRenderer = function (instance, td, row, col, prop, value, cellProperties) {
	Handsontable.TextCell.renderer.apply(this, arguments);
	$(td).css({
		background: '#fff',
		width: 104,
	});
};

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

	var height= $(window).height() - $("#planilla").position().top;
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
        		colWidths: [colWidth, colWidth, colWidth, colWidth, colWidth, colWidth, lastWidth],
        		data: data[1],
        		colHeaders: ["Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"],
        		rowHeaders: data[0],
        		columns: columns,
        		fillHandle: fill,
        		cells:function (row, col, prop) {
					var cellProperties= {};
					if(this.instance.getData()[row][col] === '')
						cellProperties.readonly=true;
					cellProperties.renderer= negativeValueRenderer;

					return cellProperties;
				}
        	});

        	grid.addHook('afterChange', function(change){
        		getChanges(change, grid.getData());
        	})
        }
    });
	

	var selectCurrentWeek = function() {
	    window.setTimeout(function () {
	        $('.week-picker').find('.ui-datepicker-current-day a').addClass('ui-state-active');
	    }, 1);
	};

	$('.week-picker').keydown(function(e){
		e.preventDefault();
		return;
	}).datetimepicker({
		format      : 'Y-m-d 00:00',
		timepicker:false,
		/*onShow:function( ct ){
			var begin	= $begin.val().split(" "),
				end		= $end.val().split(" ");
			this.setOptions({
				maxDate: $end.val() ? end[0]:0
			});
			this.setOptions({
				maxTime: begin[0] == end[0] ? end[1]:false
			});
		},
		onSelectDate: function(){
			var begin	= $begin.val().split(" "),
				end		= $end.val().split(" ");
			this.setOptions({
				maxTime: begin[0] == end[0] ? end[1]:false
			});
		},*/
	});
	
	/*$('.week-picker').datepicker({
		firstDay: 1,
	    showOtherMonths: true,
	    selectOtherMonths: true,
	    defaultDate: startDate,
	    onSelect: function(dateText, inst) {
	    	var date = $(this).datepicker('getDate');
	        startDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 1);
	        endDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 6);
	        var dateFormat = "dd-mm-yy";
	        $('.week-picker').val($.datepicker.formatDate( dateFormat, startDate, inst.settings ));
	        selectCurrentWeek();
	    },
	    beforeShowDay: function(date) {
	        var cssClass = '';
	        if(date >= startDate && date <= endDate)
	            cssClass = 'ui-datepicker-current-day';
	        return [true, cssClass];
	    },
	    onChangeMonthYear: function(year, month, inst) {
	        selectCurrentWeek();
	    }
	});*/
	
	/*$('body').on('mouseover', '#ui-datepicker-div .ui-datepicker-calendar tr', function() { $(this).find('td a').addClass('ui-state-hover'); });
	$('body').on('mouseout','#ui-datepicker-div .ui-datepicker-calendar tr', function() { $(this).find('td a').removeClass('ui-state-hover'); });*/
	
});

function save(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(grid.handsontable("getData")), fecha: $(".week-picker").val()},
		type: "POST",
		success:function(){
			window.location.href = parent + "member/planillas?page=1&size=10";
		}
	});
}

function updateTurnos(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(grid.handsontable("getData")), id: planillaId},
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
