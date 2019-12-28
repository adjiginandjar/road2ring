
$(document).on('click', '.btn-embed', function() {
    $('#publishConfirm').popup('show');
    return false;
});

$(document).on("click", '#publishConfirm .do-it', function() {
  var link = $('.embed-input').val();
  var iframe = new RegExp('<iframe(.+)</iframe>');

  if(iframe.test(link)){
    var pattern = /src="([^"]+)"/
    var match = pattern.exec(link);
    var url = match[1];
    $('#embed-map').next().removeClass("collapse")
    $('#embed-map').next().attr("src",url)
    $('#hiddenMapLink').val(url);
  }

  $('#publishConfirm').popup('hide');
});

$(document).on('click', '#publishConfirm .cancel', function(event) {
    /* Act on the event */
    $('#publishConfirm').popup('hide');
});

function setVenueGMaps(){
  var link = $('#hiddenMapLink').val()
  if(link != null && link != ''){
    $('#embed-map').next().removeClass("collapse")
    $('#embed-map').next().attr("src",link)
  }
}

var index = $('.itinerary-event').length != 0 ? $('.itinerary-event').length : 0;

$(document).on("click", "#add-event", function(event) {
    if($(".durations").val() != '' && $(".durations").val() != 0){
        drawHotelForm('','','','','','');
        index++;
    }else{
        alert("Durasi tidak boleh kosong");
    }
});

function drawHotelForm(hiddenId, picture, nama, alamat, bintang){
var formGroup = $('#hotel-form')
  var hiddenId = $('<input>', {
    'id': 'id['+index+']',
    'type': 'hidden',
    'value': 0,
    'name': 'hotels['+index+'].id'
  })

  var hotelField = $('<div>',{
    'class': 'hotel-field'
  })

  var hr = $('<hr>', {
    'style': 'border: none !important;'
  })

  hotelField.append(hiddenId)
  hotelField.append(drawPictureField('picture-'+index, picture))
  hotelField.append(drawNameField('name-'+index, nama))
  hotelField.append(drawAddressField('description-'+index, alamat))
  hotelField.append(drawStarField('star-'+index, bintang))
  hotelField.append(drawDeleteButton())

  hotelField.append(hr)

  formGroup.append(hotelField);

  upload_trip("/api/trip/upload_trip", "picture-"+index,"#picture-"+index, "#hidden_picture-"+index,640,640, "potrait");
}

function drawPictureField(section, value) {
  var formGroup = $('<div>', {
    'class': 'form-group'
  })

  var label = $('<label>', {
    'for': section
  }).text('Picture')

  formGroup.append(label);

  var wrapFile = $('<div>', {
    'class': 'wrap-file'
  })

  var inputFile = $('<input>', {
    'type': 'file',
    'id': section,
  })

  wrapFile.append(inputFile)
  formGroup.append(wrapFile)

  var inputHidden = $('<input>', {
    'type': 'hidden',
    'id': 'hidden_'+section,
    'name': 'hotels['+index+'].picture',
    'value': value,
  })

  formGroup.append(inputHidden)

  var wrapPicture = $('<div>', {
    'class': 'wrap_'+ section +' collapse',
  })

  var progress = $('<div>', {
    'class': 'progress'
  })

  var progressBar = $('<div>', {
    'aria-valuemax': 100,
    'aria-valuemin': 0,
    'aria-valuenow' : '',
    'class': 'progress-bar progress-bar-info progress-bar-striped',
    'role': 'progressbar',
    'style': ''
  })

  progress.append(progressBar)
  wrapPicture.append(progress)

  var msg = $('<div>', {
    'class': 'msg collapse text-info'
  })

  var icon = $('<i>', {
    'class': 'fa fa-check'
  })

  msg.append(icon)
  msg.append(" Upload Succedd")
  wrapPicture.append(msg)

  var preImg = $('<div>', {
    'class': 'pre_img'
  })

  var imgPre = $('<img>', {
    'alt': '',
    'src': ''
  })

  preImg.append(imgPre)
  wrapPicture.append(preImg)
  formGroup.append(wrapPicture)

  var wrapImgPict = $('<div>', {
    'class': 'wrap-img-'+section+' p-top-sm',
  })

  var img = $('<img>', {
    'src': value
  })

  wrapImgPict.append(img)
  formGroup.append(wrapImgPict)

  return formGroup;
}

function drawNameField(section, value){
  var formGroup = $('<div>', {
    'class': 'form-group'
  })

  var label = $('<label>', {
    'for': section
  }).text('Name')

  var input = $('<input>', {
    'class': 'form-control',
    'placeholder': 'Enter Hotel Name',
    'type': 'text',
    'id': section,
    'name': 'hotels['+index+'].nama',
    'value': value,
    'required': true,
  })
  return formGroup.append(label).append(input)
  }

  function drawStarField(section, value){
    var formGroup = $('<div>', {
      'class': 'form-group'
    })

    var label = $('<label>', {
      'for': section
    }).text('Star')

    var input = $('<input>', {
      'class': 'form-control',
      'placeholder': 'Enter Hotel Star',
      'type': 'number',
      'max': '5',
      'id': section,
      'name': 'hotels['+index+'].star',
      'value': value,
      'required': true,
    })

  return formGroup.append(label).append(input)
}

function drawAddressField(section, value){
  var formGroup = $('<div>', {
    'class': 'form-group'
  })

  var label = $('<label>', {
    'for': section
  }).text('Address')

  var input = $('<textarea>', {
    'class': 'form-control',
    'placeholder': 'Enter Address',
    'id': section,
    'name': 'hotels['+index+'].alamat',
    'required': true
  }).text(value)

  return formGroup.append(label).append(input)
}

function drawDeleteButton(){
  var button = $('<button>', {
    'id': 'delete-form',
    'style': 'margin: 2px 0px; height: 30px;',
    'type': 'button',
    'class': 'btn btn-default btn-sm m-bottom-xs btn-add-tab btn-del-margin',
  })

  var icon = $('<i>', {
    'class': 'fa fa-minus fa-lg',
  });

  return button.append(icon)
}

$(document).on("click", "#delete-form", function(event) {
  var container = $(this).parent();
  var indDel = $('.list-del input').length > 0 ? $('.list-del input').length : 0;
  if ($('.list-del').length > 0) {
    var delId = container.find(':first-child').val();
    if(delId == null || delId == ''){
      delId=0;
    }
    var hiddenDelId = $('<input>', {
       'type': 'hidden',
       'name': 'deletedHotel[' + indDel + '].id',
       'value': delId
    });
    $('.list-del').append(hiddenDelId);
  }

  container.remove()
})