$(document).ready( function () {
	 if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {    				$("div.mobile-tbl").addClass("mbl-tbl");    			}	 var table = $('#rsp-tbl').DataTable({
	 "dom": '<"row"<"col-sm-2"<"newRecord">><"col-sm-10"<"toolbar">>><"row"<"col-sm-12"tr>><"row"<"col-sm-6"i><"col-sm-6"p>>',
			"sAjaxSource": "/api/headline/data",
			"sAjaxDataProp": "",
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
                $(td).attr('data-th', 'Title');
            }},
			    { "mData": "linkUrl",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Link Url');
            }},
			    { "mData": "id",
            "width": "10%",
            "searchable": false,
            "orderable": false,
            "createdCell": function(td, cellData, rowData, row, col) {
               var image = "icon-icon_action";
               var text = "Action ";

               if (rowData.isPublished == "PUBLISHED" || rowData.isPublished == "EDITED") {
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

               var list = $('<ul>', {'class':'dropdown-menu'}).append(drawListAction(rowData, cellData));
               var element = $('<div>', {'class':'dropdown'}).append(button).append(list).add(hiddenId);

               $(td).html(element);
               $(td).attr('data-th', 'Action');

           }
         },
			],
			"columnDefs": [ {
        "searchable": false,
        "orderable": true,
        "targets": 0
      } ],
      "columnDefs": [{
           "targets": 2,
           "render": function(data, type, row) {
               var base_url = window.location.origin;
               return '<a href="'+ data +'">'+ data +'</a>'
           }
       }],
      "order": [[ 0, "asc" ]],

	 });

	 table.on( 'draw.dt', function () {
    var PageInfo = $('#rsp-tbl').DataTable().page.info();
    table.column(0, { page: 'current' }).nodes().each( function (cell, i) {
       cell.innerHTML = i + 1 + PageInfo.start;
    } );

    var length = PageInfo.recordsTotal;
            console.log(length)
        var btnNew = '<a href="/headline/add" class="btn btn-default btn-sm"><span class="fa fa-plus-circle fa-lg"></span> Add New Record</a>';
        if(length == 0)
          $("div.newRecord").html(btnNew);



  });

  var btnNew = '<a href="'+window.location.pathname+'/add" class="btn btn-default btn-sm"><span class="fa fa-plus-circle fa-lg"></span> Add New Record</a>';
  var filterStatus = 'Filter by : <select class="form-control isIncluded"><option value="">--- All Status ---</option><option value="true">Include</option><option value="false">Not Include</option></select>';
//  var filterCaptain = '&nbsp;<input class="form-control findCaptain" size="24" type="text" name="findCaptain" placeholder="Find Specific Captain">';
  var filterTitle = '&nbsp;<input class="form-control findTitle" size="47" type="text" name="findTitle" placeholder="Find Specific Facility Name">';
//  var filter = filterTitle;

  $("div.newRecord").html(btnNew);
  $("div.toolbar").html(filter);

  $('.isIncluded').on('change', function(event){
    if ($(this).val() != "")
        table.columns(2).search(this.value).draw();
    else
        table.columns(2).search('').draw();
  })

  $('.findTitle').on('keyup', function(event) {
      if ($(this).val().length > 2)
          table.columns(1).search(this.value).draw();
      else
          table.columns(1).search('').draw();
  });
  $('.findCaptain').on('keyup', function(event) {
      if ($(this).val().length > 2)
          table.columns(3).search(this.value).draw();
      else
          table.columns(3).search('').draw();
  });
});

 function drawListAction(rowData, cellData) {
  //Draw button Edit
  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
  var textEdit =$('<span>').append( $('<a>', {
                              'text':'Edit ',
                              'href': '/headline/edit?id=' + cellData,
                          }));
  var btnEdit = $('<li>').append(iconEdit).append(textEdit);

//  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
//  var textFacility =$('<span>').append( $('<a>', {
//                                'text':'Facility ',
//                                'href': '/trip/'+cellData+'/facility',
//                            }));
//  var btnFacility= $('<li>').append(textFacility);
//

//  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
//  var textIternary =$('<span>').append( $('<a>', {
//                                'text':'Iternary ',
//                                'href': '/trip/'+cellData+'/itinerary',
//                            }));
//  var btnIternary= $('<li>').append(textIternary);

  //Draw buttom Publish
//  var iconPublish =$('<span>').append($('<i>', {'class':'icon-icon_publish'}));
//  var textPublish =$('<span>').append( $('<a>', {
//                                  'text':'Publish ',
//                                  'href': '',
//                              }));
//  var btnPublish = $('<li>', {'id':'publishContent'}).append(iconPublish).append(textPublish);

  //Draw buttom Publish
  var iconUnpublish =$('<span>').append($('<i>', {'class':'icon-icon_unpublish'}));
  var textUnpublish =$('<span>').append( $('<a>', {
                                  'text':'Unpublish',
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
  var list = btnEdit;

  if (rowData.isPublished == "PUBLISHED" || rowData.isPublished == "EDITED") {
                  btnScheduled = $('<li>', {'style':'display: none;'});
                  list = btnEdit.add(btnUnpublish);
  }
  if (rowData.isPublished == "SCHEDULED") {
      list = btnEdit.add(btnPublish).add(btnUnpublish).add(btnScheduled);
  }
  return list;
}
