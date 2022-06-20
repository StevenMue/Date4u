const setLabel = (lbl, val, type) => {
    const label = $(`#slider-${lbl}-label-${type}`);
    label.text(val);
    const slider = $(`#slider-div-${type} .${lbl}-slider-handle`);
    const rect = slider[0].getBoundingClientRect();
    if (lbl === "min") {
        label.offset({
            top: rect.top + 23,
            left: rect.left - 30
        });
    } else {
        label.offset({
            top: rect.top - 35,
            left: rect.left - 30
        });
    }
}

const setLabels = (values, type) => {
    setLabel("min", "von: " + values[0], type);
    setLabel("max", "bis: " + values[1], type);
}

const action = (type) => {
    $('#' + type).slider().on('slide', function (ev) {
        setLabels(ev.value, type);
    });
    setLabels($('#' + type).attr("data-value").split(","), type);
}
action("cm");
action("year");

$(function () {

    $(".dropdown-menu").on('click', 'a', function () {
        $("#dropdownMenuGenderButton:first-child").text($(this).text());
        $("#dropdownMenuGenderButton:first-child").val($(this).text());
        //Populate the corresponding javascript object.
        var data = {
            startYear: [[${filterData.startYear}]],
            endYear: [[${filterData.endYear}]],
            startLength: [[${filterData.startLength}]],
            endLength: [[${filterData.endLength}]],
            gender: [[${filterData.gender}]]
        };
        var buttonText = $("#dropdownMenuGenderButton").text();
        switch (buttonText) {
            case "Männlich":
                data.gender = 1;
                break;
            case "Weiblich":
                data.gender = 2;
                break;
            default:
                data.gender = 0;
        }

        var minMaxValues = $('#' + "year").attr("data-value").split(",");
        data.startYear = minMaxValues[0];
        data.endYear = minMaxValues[1];
        minMaxValues = $('#' + "cm").attr("data-value").split(",");
        data.startLength = minMaxValues[0];
        data.endLength = minMaxValues[1];
        console.log(data.startYear + ' : ' + data.endYear + ' : ' + data.startLength + ' : ' + data.endLength + ' : ' + data.gender + $("#dropdownMenuGenderButton").text());
        $.get("/search?startYear="+data.startYear+"&endYear="+data.endYear+"&startLength="+data.startLength+"&endLength="+data.endLength+"&gender="+data.gender, function (d) {
            $("body").html(d);
        });
    });

});