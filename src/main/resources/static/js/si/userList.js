$(document).ready( function () {
	 if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {    				$("div.mobile-tbl").addClass("mbl-tbl");    			}	 var table = $('#rsp-tbl').DataTable({
	 "dom": '<"row"<"col-sm-10"<"toolbar">>><"row"<"col-sm-12"tr>><"row"<"col-sm-6"i><"col-sm-6"p>>',
			"sAjaxSource": "/api/user/list-all",
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
            { "mData": "fullName",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Name');
            }},
            { "mData": "email",
            "createdCell": function(td, cellData, rowData, row, col) {
                $(td).attr('data-th', 'Email');
            }},
            { "mData": "id",
            "width": "20%",
            "searchable": false,
            "orderable": false,
            "createdCell": function(td, cellData, rowData, row, col) {
               var image = "icon-icon_action";
               var text = "Action ";


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
      },],
      "order": [[ 3, "asc" ]],

	 });
	 table.on( 'draw.dt', function () {
    var PageInfo = $('#rsp-tbl').DataTable().page.info();
    table.column(0, { page: 'current' }).nodes().each( function (cell, i) {
       cell.innerHTML = i + 1 + PageInfo.start;
    } );
  });

//  var filterCaptain = '&nbsp;<input class="form-control findCaptain" size="24" type="text" name="findCaptain" placeholder="Find Specific Captain">';
  var filterName = 'Filter by :&nbsp;<input class="form-control findName" size="47" type="text" name="findTitle" placeholder="Find Specific User Name">';
  var filterEmail = '&nbsp;<input class="form-control findEmail" size="47" type="text" name="findTitle" placeholder="Find Specific User email">';
  var filter = filterName + filterEmail;
  $("div.toolbar").html(filter);

  $('.isIncluded').on('change', function(event){
    if ($(this).val() != "")
        table.columns(2).search(this.value).draw();
    else
        table.columns(2).search('').draw();
  })

  $('.findEmail').on('keyup', function(event) {
      if ($(this).val().length > 2)
          table.columns(2).search(this.value).draw();
      else
          table.columns(2).search('').draw();
  });
  $('.findName').on('keyup', function(event) {
        if ($(this).val().length > 2)
            table.columns(1).search(this.value).draw();
        else
            table.columns(1).search('').draw();
  });


  $(document).on('click', '#publishContent', function() {
      /* Act on the event */
      var data = table.row( $(this).parents('tr') ).data()
      console.log(data)
        var hide_id = $(this).parent().parent().parent().find('input').val();
        $('#publishConfirm').popup('show');
        $('#publishConfirm input[name=api_id]').val(data.id);
      return false;
  });

  $(document).on("click", '#publishConfirm .do-it', function() {
      var dataId = $('#publishConfirm input[name=api_id]').val()
      $.post("/api/user/change-to-rc",{ id: dataId }).done(function(data) {
        window.location.reload()
      })
  });

  $(document).on('click', '#publishConfirm .cancel', function(event) {
      /* Act on the event */
      $('#publishConfirm').popup('hide');
  });
 });

 function drawListAction(rowData, cellData) {

  var iconPublish =$('<span>').append($('<i>', {'class':'icon-icon_publish'}));
  var textPublish =$('<span>').append( $('<a>', {
                                  'text':'change to RC ',
                                  'href': '',
                              }));
  var btnPublish = $('<li>', {'id':'publishContent'}).append(iconPublish).append(textPublish);


  var list = btnPublish;

  return list;
}