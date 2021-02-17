$(document).ready( function () {
	 if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
	  $("div.mobile-tbl").addClass("mbl-tbl");
	 }
	 var table = $('#rsp-tbl').DataTable({
	 "dom": '<"row"<"col-sm-2"<"newRecord">><"col-sm-10"<"toolbar">>><"row"<"col-sm-12"tr>><"row"<"col-sm-6"i><"col-sm-6"p>>',
			"sAjaxSource": "/api/trip/data",
			"sAjaxDataProp": "",
			 "responsive": true,
			"aoColumns": [
			    {"mData": "id",
            "width": "5%",
            "searchable": false,
            "orderable": false,
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'No.');
            }
          },
          { "mData": "title",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Title.');
            }
          },
			    { "mData": "duration",
            "createdCell": function(td, cellData, rowData, row, col) {
                 $(td).attr('data-th', 'Duration');
             }
          },
			    { "mData": "itineraries.length",
            "createdCell": function(td, cellData, rowData, row, col) {
                 $(td).attr('data-th', 'Itinerary');
                 $(td).html(rowData.itineraries.length == 0 ? "<span style='color:red;'>Uncomplete</span>":"Completed")
             }
          },
			    { "mData": "tripPrices.length",
            "createdCell": function(td, cellData, rowData, row, col) {
                 $(td).attr('data-th', 'Pricelist');
                 var render;
                 if(rowData.tripPrices.length < 3){
                    render = rowData.tripPrices.length != 0 ? "<span style='color:red;'>"+ rowData.tripPrices.length +" package</span>" : "<span style='color:red;'>not set</span>"
                 }else
                    render = "<span>"+ rowData.tripPrices.length +" package</span>"
                 $(td).html(render);
             }
          },
			    { "mData": "roadCaptain.fullName",
            "createdCell": function(td, cellData, rowData, row, col) {
                 $(td).attr('data-th', 'Captain');
             }
          },{ "mData": "publishedStatus",
			      "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Status');
            }
			    },{ "mData": "id",
            "width": "10%",
            "searchable": false,
            "orderable": false,
            "createdCell": function(td, cellData, rowData, row, col) {
               var image = "icon-icon_action";
               var text = "Action ";

               if (rowData.publishedStatus == "PUBLISHED" || rowData.publishedStatus == "EDITED") {
                   image = "icon-icon_published";
                   text = "Published";
               }
               var buttonIcon = $('<span>', {'class':'buttonIcon'}).append($('<i>', {'class':image}));
               var buttonText = $('<span>', {'class':'buttonText'}).append($('<a>', {'text': text}));
               var button = $('<button>', {'class':'btn btn-default dropdown-toggle', 'type':'button', 'data-toggle':'dropdown'}).append(buttonIcon).append(buttonText).append($('<span>', {'class':'caret'}));

               var hiddenId = $('<input>', {
                   'type': 'hidden',
                   'value': cellData
               });

               var list = $('<ul>', {'class':'dropdown-menu'}).append(drawListAction(td, rowData, cellData, row, col));
               var element = $('<div>', {'class':'dropdown'}).append(button).append(list).add(hiddenId);

               $(td).html(element);
               $(td).attr('data-th', 'Action');

           }
         },{ "mData": "roadCaptain.email",
              "visible": false,
              "createdCell": function(td, cellData, rowData, row, col) {
              $(td).attr('data-th', 'Email');
           }
         },
			],
			"columnDefs": [ {
                "searchable": false,
                "orderable": true,
                "targets": 0
            }],
            "columnDefs": [{
              "targets": 5,
              "render": function(data, type, row) {
                  return data != null && data != '' ? data : '' ;
              }
            }],
            "columnDefs": [{
              "searchable": true,
              "targets": 6,
              "visible": false,
            }],
            "order": [[ 6, "asc" ]],
     });
	 table.on( 'draw.dt', function () {
        var PageInfo = $('#rsp-tbl').DataTable().page.info();
        table.column(0, { page: 'current' }).nodes().each( function (cell, i) {
           cell.innerHTML = i + 1 + PageInfo.start;
        } );

//        table.column(3, { page: 'current' }).nodes().each( function (cell, i) {
//           cell.innerHTML = cell.innerHTML == 0 ? "<span style='color:red;'>Uncomplete</span>" : "Completed";
//        } );
      });

  var remoteUser = $('.remote-user').text()
  console.log(remoteUser); //col 8
  var remoteRoleIsAdmin = $('.remote-role').val()
    console.log(remoteRoleIsAdmin); //col 8
  if(remoteRoleIsAdmin=='false'){
    table.columns(8).search(remoteUser).draw();
  }

  var btnNew = '<a href="/trip/add" class="btn btn-default btn-sm"><span class="fa fa-plus-circle fa-lg"></span> Add New Record</a>';
  var filterStatus = 'Filter by : <select class="form-control isPublished"><option value="">--- All Status ---</option><option value="UNPUBLISHED">Unpublish Content</option><option value="PUBLISHED">Published Content</option></select>';
  var filterCaptain = '&nbsp;<input class="form-control findCaptain" size="24" type="text" name="findCaptain" placeholder="Find Specific Captain">';
  var filterTitle = '&nbsp;<input class="form-control findTitle" size="47" type="text" name="findTitle" placeholder="Find Specific Title">';
  var filter = filterStatus +  filterTitle + filterCaptain;
  $("div.newRecord").html(btnNew);
  $("div.toolbar").html(filter);

  $('.isPublished').on('change', function() {
    if ($(this).val() != "")
        table.columns(6).search('^'+this.value+'$', true, false).draw();
    else
        table.columns(6).search('').draw();
  });

  $('.findTitle').on('keyup', function(event) {
      if ($(this).val().length > 2)
          table.columns(1).search(this.value).draw();
      else
          table.columns(1).search('').draw();
  });
  $('.findCaptain').on('keyup', function(event) {
      if ($(this).val().length > 2)
          table.columns(5).search(this.value).draw();
      else
          table.columns(5).search('').draw();
  });

  $(document).on('click', '#publishContent', function() {
      /* Act on the event */
      var data = table.row( $(this).parents('tr') ).data()
      console.log(data)
      if(data.tripPrices == 0){
        alert("Trip Price atau Itinerary Kosong, harap input data terlebih dahulu sebelum publish")
      }else{
        var hide_id = $(this).parent().parent().parent().find('input').val();
        $('#publishConfirm').popup('show');
        $('#publishConfirm input[name=api_id]').val(data.id);
      }
      return false;
  });

  $(document).on("click", '#publishConfirm .do-it', function() {
      var dataId = $('#publishConfirm input[name=api_id]').val()
      $.post( "/api/trip/change-status/"+ dataId + "/PUBLISHED").done(function(data) {
        window.location.reload()
      })
  });

  $(document).on('click', '#publishConfirm .cancel', function(event) {
      /* Act on the event */
      $('#publishConfirm').popup('hide');
  });

  $(document).on('click', '#unpublishContent', function() {
      /* Act on the event */
      var data = table.row( $(this).parents('tr') ).data()
      $('#takeoutConfirm').popup('show');
      $('#takeoutConfirm input[name=api_id]').val(data.id);
      return false;
  });

  $(document).on("click", '#takeoutConfirm .do-it', function() {
      var dataId = $('#takeoutConfirm input[name=api_id]').val()
      $.post( "/api/trip/change-status/"+ dataId + "/UNPUBLISHED").done(function(data) {
        window.location.reload()
      })
  });

  $(document).on('click', '#takeoutConfirm .cancel', function(event) {
      /* Act on the event */
      $('#takeoutConfirm').popup('hide');
  });

});

 function drawListAction(td, rowData, cellData, row, data) {
  //Draw button Edit
  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
  var textEdit =$('<span>').append( $('<a>', {
                              'text':'Edit ',
                              'href': '/trip/edit?id=' + cellData,
                          }));
  var btnEdit = $('<li>').append(iconEdit).append(textEdit);

//  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
  var textPriceList =$('<span>').append( $('<a>', {
                                'text':'Price List ',
                                'href': '/trip/'+cellData+'/price-list',
                            }));
  var btnPriceList= $('<li>').append(textPriceList);


//  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
  var textIternary =$('<span>').append( $('<a>', {
                                'text':'Iternary ',
                                'href': '/trip/'+cellData+'/itinerary',
                            }));
  var btnIternary= $('<li>').append(textIternary);

  //Draw buttom Publish
  var iconPublish =$('<span>').append($('<i>', {'class':'icon-icon_publish'}));
  var textPublish =$('<span>').append( $('<a>', {
                                  'text':'Publish ',
                                  'id': 'publish-btn',
                                  'href': ''
                              }));
  var btnPublish = $('<li>', {'id':'publishContent'}).append(iconPublish).append(textPublish);

  //Draw buttom Publish
  var iconUnpublish =$('<span>').append($('<i>', {'class':'icon-icon_unpublish'}));
  var textUnpublish =$('<span>').append( $('<a>', {
                                  'text':'Unpublish',
                                  'id': 'unpublish-btn',
                                  'href': '',
                              }));
  var btnUnpublish = $('<li>', {'id':'unpublishContent'}).append(iconUnpublish).append(textUnpublish);

  //Draw button Scheduled
//  var iconScheduled = $('<span>').append($('<i>', {'class':'icon-icon_schedule_post'}));
//  var textScheduled =$('<span>').append( $('<a>', {
//                                      'text':'Schedule Publish',
//                                      'href': '',
//                                      }));
//  var btnScheduled = $('<li>', {'id':'schedule'}).append(iconScheduled).append(textScheduled);
  var list = btnEdit.add(btnIternary).add(btnPriceList).add(btnPublish);
//  .add(btnPublish);

  if (rowData.publishedStatus == "PUBLISHED" || rowData.publishedStatus == "EDITED") {
                  btnScheduled = $('<li>', {'style':'display: none;'});
                  list = btnEdit.add(btnIternary).add(btnPriceList).add(btnUnpublish);
  }
  if (rowData.isPublished == "SCHEDULED") {
      list = btnEdit.add(btnPublish).add(btnUnpublish).add(btnScheduled);
  }

  return list;
}