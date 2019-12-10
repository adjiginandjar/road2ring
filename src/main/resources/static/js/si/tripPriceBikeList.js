if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
$("div.mobile-tbl").addClass("mbl-tbl");
}
var savedRow = document.getElementById('rsp-tbl').rows.length;
var urlSplit = window.location.pathname.split('/')
var addedCount = 0;
var rowIndexSaved = [];
$('#new').click( function (e) {
      e.preventDefault();
      var tableRef = document.getElementById('rsp-tbl').getElementsByTagName('tbody')[0];
      var row= document.createElement("tr");
      var cellCount = document.getElementById('rsp-tbl').rows[0].cells.length

      addCell(row, cellCount, tableRef)

      $('.btn-container').removeClass('collapse')


//      console.log(document.getElementById('rsp-tbl').rows.length)
} );

function addCell(row, cellCount, tbody){
    var rowCount = document.getElementById('rsp-tbl').rows.length;
    for(var i=0; i<cellCount; i++){
    var td = document.createElement("td");
        if(i == 0){
            td.innerHTML = rowCount
        }
        else if(i == 1){
            td.innerHTML = "<input type='text' placeholder='nama motor' id='title-"+rowCount+"' class='new-"+rowCount+"'/> <input id='hidden_motor-"+rowCount+"' type='hidden' name='bikeId' class='new-"+rowCount+" bike-"+rowCount+"'/> <input id='hidden_motors-"+rowCount+"' type='hidden' name='bikeId' class='new-"+rowCount+" bike-"+rowCount+"'/>"
        }else if(i == 4){
            td.innerHTML = "<input type='number' placeholder='price' name='price' class='new-"+rowCount+" price-"+rowCount+"'/> <input id='hidden_price-"+rowCount+"' type='hidden' name='stock' class='new-"+rowCount+" price-"+rowCount+"'/>"
        }else if(i == 5){
            td.innerHTML = "<input type='number' placeholder='stock' name='stock' class='new-"+rowCount+" stock-"+rowCount+"'/> <input id='hidden_stock-"+rowCount+"' type='hidden' name='price' class='new-"+rowCount+" stock-"+rowCount+"'/>"
        }else if(i == 6){
            td.innerHTML = "<button id='add-"+rowCount+"' class='btn btn-success btn-md' index = "+rowCount+">ADD</button> <button id='cancel-"+rowCount+"' class='btn btn-danger btn-md collapse' index = "+rowCount+">CANCEL</button>";

        }

        row.appendChild(td)
    }
    tbody.appendChild(row)
    $('#add-'+rowCount).click( function (e) {
        if($(".bike-"+rowCount).val() != '' && $(".price-"+rowCount).val() != '' && $(".stock-"+rowCount).val() != ''){
            $("input.new-"+rowCount).attr("disabled", true);
            $(this).addClass('collapse')
            $(this).next().removeClass('collapse')
            $("#hidden_price-"+rowCount).val($(".price-"+rowCount).val())
            $("#hidden_stock-"+rowCount).val($(".stock-"+rowCount).val())
            $("#hidden_motors-"+rowCount).val($(".bike-"+rowCount).val())
            rowIndexSaved.push(rowCount)
            addedCount += 1
        }else{
            alert("Harap mengisi form terlebih dahulu sebelum menambahkan.")
        }
    })
    $('#cancel-'+rowCount).click( function (e) {
        var index = rowIndexSaved.indexOf(rowCount);
        $("input.new-"+rowCount).attr("disabled", false);
        $(this).addClass('collapse')
        $(this).prev().removeClass('collapse')
        $("#hidden_price-"+rowCount).val("")
        $("#hidden_stock-"+rowCount).val("")
        $("#hidden_motors-"+rowCount).val("")
        addedCount -= 1
        if (index > -1) {
           rowIndexSaved.splice(index, 1);
        }
    })

    autocomplete(rowCount)

}

$('#cancel-btn').click( function (e) {
//    var failData = [12,31,21]
//    alert("Sebagian data yang anda masukkan gagal disimpan, silahkan periksa kembali data tersebut.\n"+failData)
    location.reload()
})

var failData = [];
$('#save-btn').click( function (e) {
    var idList = [];
    var priceList = [];
    var stockList = [];
    for(var i = 0; i<rowIndexSaved.length; i++){
        idList.push($("#hidden_motors-"+rowIndexSaved[i]).val());
        priceList.push($("#hidden_price-"+rowIndexSaved[i]).val());
        stockList.push($("#hidden_stock-"+rowIndexSaved[i]).val());
    }
    callAPI(idList, priceList, stockList, rowIndexSaved[i])
})

function callAPI(id, price, stock, index){
    var message = ""
    var formData = new FormData()
    formData.append("id", id)
    formData.append("price", price)
    formData.append("stock", stock)
    $.ajax({
        type:"POST",
        url:'/api/trip/'+urlSplit[2]+'/price-list/'+urlSplit[4]+'/bike/save',
        data:formData,
        processData: false,
        contentType: false,
    }).done(function(data){
        message = data.message;
        if(message != 'sukses'){
            alert(message);
        }
        location.reload()
    })

    return message;
}

function autocomplete(rowCount){
var hostname = $(location).attr('protocol') + '//' + $(location).attr('host');
var api = hostname + "/api";
    $('#title-'+rowCount).autocomplete({
      autoFocus: true,
      selectFirst: true,
      source: function(request, response) {
        var arrdata = [];
        var json_data = $.getJSON(api + '/motor/autocomplete', {
            keyword: request.term
        });
        json_data.done(function(data) {
            //if (!$.isEmptyObject(data)) {
            $.each(data.object, function(key, value) {
                arrdata.push(value)
            });
            response($.map(arrdata, function (value, key) {
                return{
                    label: value.title,
                    value: value.id
            }
            }));
        });
    },
      select: function(event,ui){
        if(!ui.item){
          this.value = ''
          $("#hidden_motor-"+rowCount).val("")
        }else{
          $(this).val(ui.item.label)
          $("#hidden_motor-"+rowCount).val(ui.item.value)
        }
        return false
      },
      focus: function(event, ui){
        return false;
      },
      change: function (event, ui) {
          if (!ui.item) {
              this.value = '';
              $("#hidden_motor-"+rowCount).val("")
          }
      },
    minLength: 2,
    delay: 100
    });
}




