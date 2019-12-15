function slugify(text) {
    text = text.replace(/[^-a-zA-Z0-9,&\s]+/ig, '');
    text = text.replace(/-/gi, "_");
    text = text.replace(/\s/gi, "-");
    return text;
}

function upload_trip(url_upload, section,image_field, hidden_field,width,height,type) {
    $(image_field).html5Uploader({
        name: "file",
        postUrl: url_upload+"/"+type,
        onClientLoadStart: function(e, file) {
            var warp = '.wrap-'+section;
            $(hidden_field).empty();
            $('.wrap-img-'+section).empty();
            $('.wrap_'+section+' .pre_img img').attr('src', '');
            $('.wrap_'+section).addClass('collapse');
            $('.wrap_'+section+' .msg').removeAttr('style');
            $('.wrap_'+section+' .progress').removeAttr('style');
        },
        onClientLoad: function(e, file) {
            $(".wrap_"+section).removeClass('collapse');
        },
        onServerLoadStart: function(e, file) {
            $('.wrap_'+section+' .progress-bar').attr({
                'aria-valuenow': 0,
                'style': 'width : 0'
            });
        },
        onServerProgress: function(e, file) {
            if (e.lengthComputable) {
                var percentComplete = (e.loaded / e.total) * 80;
                $('.wrap_'+section+' .progress-bar').attr({
                    'aria-valuenow': percentComplete,
                    'style': 'width : ' + percentComplete + '%'
                });
            }
        },
        onServerLoad: function(e, file) {
            $('.wrap_'+section+' .progress-bar').attr({
                'aria-valuenow': 95,
                'style': 'width : 95%'
            });
        },
        onSuccess: function(m, i, responseText) {
            var json = $.parseJSON(responseText);
            $('.wrap_'+section+' .progress-bar').attr({
                'aria-valuenow': 100,
                'style': 'width : 100%'
            }).parent().delay(500).fadeOut('fast', function() {
                $('.wrap_'+section+' .msg').fadeIn('slow');
            });
            var hidden_val = '';
            var isValidImg = true;

            if (responseText != "") {
                hidden_val = json.object;

//                console.log($('.wrap_'+section+' .pre_img img'))
//                console.log('/img/assets/'+json.object)

                if(json.code === 600 ){
                  isValidImg = true;
                  $('.wrap_'+section+' .pre_img img').attr('src',json.object);
                }else{
                  isValidImg = false;
                }

            }
            if(isValidImg)
              $(hidden_field).val(hidden_val);
            else
              alert("Size Image kebesaran, tidak boleh lebih dari 100Kb")

        }
    });
}

function upload_gallery(url_upload, image_field, page) {
    $(image_field).html5Uploader({
        name: "file",
        postUrl: url_upload+"/potrait",
        onClientLoadStart: function(e, file) {
          var elMedia = drawElMedia(file.name);
          $('.list-tipe .custom-file').append(elMedia);
        },
        onClientLoad: function(e, file) {},
        onServerLoadStart: function(e, file) {
            $('.custom-file #' + slugify(file.name) + ' .progress-bar').attr({
                'aria-valuenow': 0,
                'style': 'width : 0'
            });
        },
        onServerProgress: function(e, file) {
            $('#up2').prop('disabled', true);
            if (e.lengthComputable) {
                var percentComplete = (e.loaded / e.total) * 80;
                $('.custom-file #' + slugify(file.name) + ' .progress-bar').attr({
                    'aria-valuenow': percentComplete,
                    'style': 'width : ' + percentComplete + '%'
                });
            }
        },
        onServerLoad: function(e, file) {
            $('.custom-file #' + slugify(file.name) + ' .progress-bar').attr({
                'aria-valuenow': 100,
                'style': 'width : 100%'
            });


        },
        onSuccess: function(m, i, responseText) {

            var indexEl = $('.push').length - 1;
            var json = $.parseJSON(responseText);
            if(json.code == 600){
              var selIndex = '.custom-file #' + slugify(i.name);
              $(selIndex + ' .progress').delay(500).fadeOut('fast', function() {
                  $(selIndex + ' .msg').fadeIn('slow');
                  $(selIndex + ' .btn').fadeIn('slow');
              });
              var hidden_val = "";
              var thumbnail = "";
              var base_url = window.location.origin;
              if (responseText != "") {
                  hidden_val = json.object;
                  thumbnail = json.object;
              }
              $(selIndex + ' .picture').val(base_url + '/img/assets/'+ hidden_val);
              $(selIndex + ' .picture_thumb').val(base_url + '/img/assets/'+ thumbnail);
              $(selIndex + ' .pre_img img').attr('src', base_url + '/img/assets/'+ thumbnail);
              $('.push').each(function(index, el) {
                  $(this).find('.title').attr('name', 'listMedia[' + index + '].title');
                  $(this).find('.picture').attr('name', 'listMedia[' + index + '].picture');
                  $(this).find('.type').attr('name', 'listMedia[' + index + '].type');
                  $(this).find('.link').attr('name', 'listMedia[' + index + '].link');
              });
            }
            else{
               alert("Size Image kebesaran, tidak boleh lebih dari 100Kb")
               $('#'+slugify(i.name)).remove();
            }
              $('#up2').prop('disabled', false);
        }
    });
}

function drawElMedia(fname) {
    var elMediaWrap = $('<div>', {
            'id': slugify(fname),
            'class': 'push col-md-4'
        }),
        innerSt = $('<div>', {
            'class': 'cont-push m-bottom-sm'
        }),
        hiddenLink = $('<input>', {
            'type': 'hidden',
            'id': 'hidden_picture',
            'class': 'picture',
            'value': ''
        }),
        hiddenLinkThumb = $('<input>', {
            'type': 'hidden',
            'id': 'hidden_picture_thumb',
            'class': 'picture_thumb',
            'value': ''
        }),
        progress = $('<div>', {
            'class': 'progress'
        }).append($('<div>', {
            'class': 'progress-bar progress-bar-info progress-bar-striped',
            'role': 'progressbar',
            'aria-valuenow': '',
            'aria-valuemin': '0',
            'aria-valuemax': '100'
        })),
        msg = $('<div>', {
            'class': 'msg collapse text-info'
        }).append($('<i>', {
            'class': 'fa fa-check-circle'
        })),
        img = $('<div>', {
            'class': 'pre_img'
        }).append($('<img>', {
            'src': '',
            'alt': ''
        })),
        btnRemove = $('<button>', {
            'type': 'button',
            'class': 'btn btn-sm btn-danger collapse'
        }).text('Remove'),
        title = $('<div>', {
            'class': 'm-bottom-xs'
        }).append($('<input>', {
            'type': 'text',
            'class': 'form-control title',
            'placeholder': 'Title'
        })),
        source = $('<div>', {
          'class': 'm-bottom-xs'
        }).append($('<input>', {
          'type': 'text',
          'class': 'form-control source',
          'placeholder': 'Source'
        })),
        typeMedia = $('<div>', {
            'class': 'm-bottom-xs'
        }).append($('<select>', {
            'class': 'form-control type'
        }).append($('<option>', {
            'value': 'IMAGE'
        }).text('Picture').add($('<option>', {
            'value': 'YOUTUBE'
        }).text('Video')))),
        link = $('<div>', {
            'class': 'm-bottom-xs'
        }).append($('<input>', {
            'type': 'text',
            'class': 'form-control link',
            'disabled': true,
            'placeholder': 'Youtube Link'
        }));

    var tpl_view = elMediaWrap.append(innerSt.append(hiddenLink).append(progress).append(msg).append(img).append(btnRemove)).append(title).append(typeMedia).append(link);
    return tpl_view;
}