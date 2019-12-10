$(document).ready( function () {
//	 var tripId = window.location.pathname.split('/')
//	 console.log(tripId[2])
	 if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {    				$("div.mobile-tbl").addClass("mbl-tbl");    			}	 var table = $('#rsp-tbl').DataTable({
	 "dom": '<"row"<"col-sm-2"<"newRecord">><"col-sm-10"<"toolbar">>><"row"<"col-sm-12"tr>><"row"<"col-sm-6"i><"col-sm-6"p>>',
			"sAjaxSource": "/api/transaction/data",
			"sAjaxDataProp": "",
			"autoWidth" : false,
			"aoColumns": [
			    {"mData": "id",
            "width": "5%",
            "searchable": false,
            "orderable": false,
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'No.');
            }
          },
          { "mData": "code",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Code');
            }},
			    { "mData": "paymentStatus",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Status');
            }},
			    { "mData": "trip.title",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Trip');
            }},
			    { "mData": "created",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Created Date');
            }},
			    { "mData": "createdBy",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Created By');
            }},
//			    { "mData": "updated"},
			    { "mData": "id",
            "width": "10%",
            "searchable": false,
            "orderable": false,
            "createdCell": function(td, cellData, rowData, row, col) {
               var text = "Edit";

               var buttonText = $('<span>', {'class':'buttonText'}).append($('<a>', {'text': text, 'href': '/transaction/edit?id='+ cellData}));
               var button = $('<button>', {'class':'btn btn-default', 'type':'button'}).append(buttonText);

               var hiddenId = $('<input>', {
                   'type': 'hidden',
                   'value': cellData
               });

//               var list = $('<ul>', {'class':'dropdown-menu'}).append(drawListAction(rowData, cellData));
               var element = $('<div>', {'class':'dropdown'}).append(button).add(hiddenId);

               $(td).html(element);
               $(td).attr('data-th', 'Action');

           }
         },],
			"columnDefs": [ {
            "searchable": false,
            "orderable": true,
            "targets": 0
         },{
             "targets":4,
             "render": function(data, type, row) {
                  var parseTs = moment(data, 'x');
                  return data != null && data != '' ? moment(parseTs).format('DD/MM/YYYY') : '-'
              }
         },],
      "order": [[ 2, "asc" ]],

	 });
	 table.on( 'draw.dt', function () {
    var PageInfo = $('#rsp-tbl').DataTable().page.info();
    table.column(0, { page: 'current' }).nodes().each( function (cell, i) {
       cell.innerHTML = i + 1 + PageInfo.start;
    } );
  });
//
//  var btnNew = '<a href="'+window.location.pathname+'/add" class="btn btn-default btn-sm"><span class="fa fa-plus-circle fa-lg"></span> Add New Record</a>';
  var filterStatus = 'Filter by : <select class="form-control isIncluded"><option value="">--- All Status ---</option><option value="BOOKED">BOOKED</option><option value="PAID">PAID</option><option value="CANCEL">CANCEL</option><option value="FAILED">FAILED</option><option value="WAITING">WAITING</option></select>';
////  var filterCaptain = '&nbsp;<input class="form-control findCaptain" size="24" type="text" name="findCaptain" placeholder="Find Specific Captain">';
  var filterTitle = '&nbsp;<input class="form-control findTitle" size="47" type="text" name="findTitle" placeholder="Type to find">';
  var filter = filterStatus + filterTitle;
//  $("div.newRecord").html(btnNew);
  $("div.toolbar").html(filter);
//
  $('.isIncluded').on('change', function(event){
    if ($(this).val() != "")
        table.columns(2).search(this.value).draw();
    else
        table.columns(2).search('').draw();
  })
//
  $('.findTitle').on('keyup', function(event) {
      table.search( this.value ).draw();
  });
//  $('.findCaptain').on('keyup', function(event) {
//      if ($(this).val().length > 2)
//          table.columns(3).search(this.value).draw();
//      else
//          table.columns(3).search('').draw();
//  });
});

 function drawListAction(rowData, cellData) {
  //Draw button Edit
  var iconEdit = $('<span>').append($('<i>', {'class':'icon-icon_edit'}));
  var textEdit =$('<span>').append( $('<a>', {
                              'text':'Edit ',
                              'href': '/trip/edit?id=' + cellData,
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