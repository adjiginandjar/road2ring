index = $('.itinerary-event').length != 0 ? $('.itinerary-event').length : 0;
//var groupOnLoad = $("#group").val()

var previous = 0;

$(document).on("focus", "#group", function(event) {
   previous = this.value
})
$(document).on("change", "#group", function(event) {
    var uri = window.location.pathname.split('/');

    $.get( "/api/trip/"+uri[2]+ '/itinerary/'+ $(this).val()).done(function(data) {
      if(data.length != 0){
        window.location.href = "/trip/"+uri[2]+ '/itinerary/edit?id='+$('#group').val();
      }else{
        if(previous != 'Select'){
          $('#group').val(previous)
        }
      }
    }).fail(function(data){
      console.log(previous)
      $('#group').val(previous)
    })
});

 $(document).on("click", "#itinerarySaveBtn", function(event){
    tinyMCE.triggerSave();
    setInputHidden()
    var uri = window.location.pathname.split('/');
    if($("#itineraryForm").parsley().validate() === true){
      $('#itinerarySaveBtn').prop('disabled', true);
      $.post( "/trip/"+uri[2]+ '/itinerary/save', $("#itineraryForm").serialize()).done(function(data) {
        window.location.href = "/trip/"+uri[2]+ '/itinerary';
      })
    }
 })

$(document).on("click", "#add-event", function(event) {
  if($('#group').val() == 'Select'){
    alert('Please Choose the Day')
  }else{
    if($('#itinerarySaveBtn').hasClass('collapse')){
      $('#itinerarySaveBtn').removeClass('collapse')
    }
    drawItineraryEvent('','','','','','');
    index++;
  }
});

function drawItineraryEvent(hiddenId, picture, eventVal, description, group, groupTitle){
var formGroup = $('#itinerary-form')
  var hiddenId = $('<input>', {
    'id': 'id['+index+']',
    'type': 'hidden',
    'value': 0,
    'name': 'itineraries['+index+'].id'
  })

  var itineraryEvent = $('<div>',{
    'class': 'itinerary-event'
  })

  var hr = $('<hr>', {
    'style': 'border: none !important;'
  })

  itineraryEvent.append(hiddenId)
  itineraryEvent.append(drawPictureField('picture-'+index, picture))
  itineraryEvent.append(drawEventField('event-'+index, eventVal))
  itineraryEvent.append(drawDescriptionField('description-'+index, description))
  itineraryEvent.append(drawHiddenValue('group', group))
  itineraryEvent.append(drawHiddenValue('groupTitle', groupTitle))
  if(index != 0){
    itineraryEvent.append(drawDeleteButton())
  }

  itineraryEvent.append(hr)

  formGroup.append(itineraryEvent);

  upload_trip("/api/trip/upload_trip", "picture-"+index,"#picture-"+index, "#hidden_picture-"+index,640,640, "potrait");
  general_editor()
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
    'name': 'itineraries['+index+'].imageUrl',
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

function drawEventField(section, value){
  var formGroup = $('<div>', {
    'class': 'form-group'
  })

  var label = $('<label>', {
    'for': section
  }).text('Event')

  var input = $('<input>', {
    'class': 'form-control',
    'placeholder': 'Enter Event Title',
    'type': 'text',
    'id': section,
    'name': 'itineraries['+index+'].title',
    'value': value,
    'required': true,
  })

  return formGroup.append(label).append(input)
}

function drawDescriptionField(section, value){
  var formGroup = $('<div>', {
    'class': 'form-group'
  })

  var label = $('<label>', {
    'for': section
  }).text('Description')

  var input = $('<textarea>', {
    'class': 'form-control mce-general',
    'placeholder': 'Enter Description',
    'id': section,
    'name': 'itineraries['+index+'].description',
    'required': true
  }).text(value)

  return formGroup.append(label).append(input)
}

function drawHiddenValue(name, value) {
  var input = $('<input>', {
    'class': name,
    'type': 'hidden',
    'value': value,
    'name': 'itineraries['+index+'].'+name
  })

  return input
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

function setInputHidden(){
 $('.groupTitle').each(function(index){
    $(this).val($('#title').val())
 })

 $('.group').each(function(index){
    $(this).val($('#group').val())
 })
}

$(document).on("click", "#delete-form", function(event) {
  var container = $(this).parent();
  var indDel = $('.list-del input').length > 0 ? $('.list-del input').length : 0;
  if ($('.list-del').length > 0) {
    var delId = container.find(':first-child').val();
    console.log(delId)
    if(delId == null || delId == ''){
      delId=0;
    }
    var hiddenDelId = $('<input>', {
       'type': 'hidden',
       'name': 'deletedItinerary[' + indDel + '].id',
       'value': delId
    });
    $('.list-del').append(hiddenDelId);
  }

  container.remove()
})