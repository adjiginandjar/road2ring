function multi_page_editor(){
  var hostname = $(location).attr('protocol') + '//' + $(location).attr('host');
  tinymce.init({
          selector: ".mce-editor",
//          force_br_newlines : true,
          force_p_newlines : true,
//          forced_root_block : 'p',
          content_style: "body {font-size:13px !important; font-family: Roboto, sans-serif !important; color: #555 !important}",
          invalid_elements: "span",
          plugins: "autoresize",
          height: 300,
          menubar: false,
          
        plugins: ["lists", "code", "paste", "hr", "link", "media", "fullscreen", "preview", "image", "wordcount"],
          theme_advanced_buttons3_add: "pastetext,pasteword,selectall",
          toolbar: "undo redo | bold italic underline | alignleft aligncenter alignright alignjustify | removeformat | code | link image | hr |  bullist numlist | media | fullscreen preview | suggested-article block-quote",
          extended_valid_elements: "+iframe[width|height|name|align|class|frameborder|allowfullscreen|allow|src|*]," +
                  "script[language|type|async|src|charset]" +
                  "img[*]" +
                  "embed[width|height|name|flashvars|src|bgcolor|align|play|loop|quality|allowscriptaccess|type|pluginspage]" +
                  "blockquote[dir|style|cite|class|id|lang|onclick|ondblclick"
                  +"|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
                  +"|onmouseover|onmouseup|title]",

          image_caption: true,
          file_picker_types: 'image',
          relative_urls : false,
          remove_script_host : false,
          document_base_url : hostname,
          keep_styles: false,
          file_picker_callback: function (cb, value, meta) {
              var input = document.createElement('input');
              input.setAttribute('type', 'file');
              input.setAttribute('accept', 'image/*');

              input.onchange = function () {
                var file = this.files[0];
                var reader = new FileReader();
                reader.onload = function (e) {
//                  var hostname = $(location).attr('protocol') + '//' + $(location).attr('host');
                  var api = hostname + "/api";
                  var formData = new FormData();
                  formData.append("file", file)
                  var json_data = new XMLHttpRequest();
                  var url = api + '/trip/upload_trip/potrait';
                  var data;

                  json_data.onreadystatechange = function() {
                      if (this.readyState == 4 && this.status == 200) {
                          var response = JSON.parse(this.responseText);
                          data = '/img/assets/'+response.object;
                          cb(data);
                          console.log($(this))
                      }
                  };

                  json_data.open("POST", url, true);

//                  console.log(formData)
                  json_data.send(formData);
                  console.log($(this))
                };
                console.log($(this))
                reader.readAsDataURL(file);
              };
              console.log($(this))
              input.click();
          },
          valid_elements : '+*[*]',

          media_live_embeds: true,
          paste_auto_cleanup_on_paste: true,
          paste_remove_styles: true,
          paste_remove_styles_if_webkit: true,
          paste_as_text: true,
          paste_data_images: true,
          paste_strip_class_attributes: true,
          setup: function (ed) {
            ed.on('NodeChange', function (e) {
                if(e.element.tagName === "IMG"){
                    console.log(e.element)
                  $(e.element).wrap("<span class='d-block img__wrap' />");
                  e.element.setAttribute("class", 'img-fluid d-block');

                }if(e.element.tagName == "P"){
                  e.element.setAttribute("class", 'container mb-0');
                }
              });

              ed.ui.registry.addButton('inlineimage', {
                  title: 'Add Image',
                  icon: 'image',
              });

              ed.on('blur', function(e){
                var iOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
                if(!iOS){
                  var text = tinyMCE.activeEditor.getContent({format : 'html'});
                  $('#'+ed.id).val(text)
                  setToHidden()
                }
              });
              ed.on('mouseout', function(e){
                var iOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
                if(iOS){
                  var text = tinyMCE.activeEditor.getContent({format : 'html'});
                  $('#'+ed.id).val(text)
                  setToHidden()
                }
              });
          },
      });
}

function general_editor(){
var hostname = $(location).attr('protocol') + '//' + $(location).attr('host');
  tinymce.init({
            selector: ".mce-general",
            forced_root_block: false,
        content_style: "body {font-size:14px !important; font-family: 'Open Sans', sans-serif !important; color: #555 !important}",
        invalid_elements: "span",
        plugins: "autoresize",
        height: 300,
        menubar: false,
        plugins: ["lists", "code", "paste", "hr", "link", "media", "fullscreen", "preview", "image", "wordcount"],
        theme_advanced_buttons3_add: "pastetext,pasteword,selectall",
        toolbar: "undo redo | h1 h2 h3 | bold italic underline | alignleft aligncenter alignright alignjustify | suggested-article block-quote kastv | link image media | bullist numlist | fullscreen preview code | hr removeformat",
        extended_valid_elements: "+iframe[width|height|name|align|class|frameborder|allowfullscreen|allow|src|*]," +
            "script[language|type|async|src|charset]" +
            "img[*]" +
            "embed[width|height|name|flashvars|src|bgcolor|align|play|loop|quality|allowscriptaccess|type|pluginspage]" +
            "blockquote[dir|style|cite|class|id|lang|onclick|ondblclick"
            + "|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
            + "|onmouseover|onmouseup|title]",

            image_caption: true,
            file_picker_types: 'image',
            relative_urls : false,
            remove_script_host : false,
            document_base_url : hostname,
            keep_styles: false,
            file_picker_callback: function (cb, value, meta) {
                var input = document.createElement('input');
                input.setAttribute('type', 'file');
                input.setAttribute('accept', 'image/*');

                input.onchange = function () {
                  var file = this.files[0];
                  var reader = new FileReader();
                  reader.onload = function () {
                    var hostname = $(location).attr('protocol') + '//' + $(location).attr('host');
                    var api = hostname + "/api";
                    var formData = new FormData();
                    formData.append("file", file)
                    var json_data = new XMLHttpRequest();
                    var url = api + '/trip/upload_trip/potrait';
                    console.log(url)
                    var data;

                    json_data.onreadystatechange = function() {
                    console.log(this.status)
                        if (this.readyState == 4 && this.status == 200) {
                            var response = JSON.parse(this.responseText);
                            data = '/img/assets/'+response.object;
                            cb(data);
                        }
                    };

                    json_data.open("POST", url, true);

                    console.log(formData)
                    json_data.send(formData);
                  };
                  console.log(file)
                  reader.readAsDataURL(file);
                };

                input.click();
            },
            valid_elements : '+*[*]',

            media_live_embeds: true,
            paste_auto_cleanup_on_paste: true,
            paste_remove_styles: true,
            paste_remove_styles_if_webkit: true,
            paste_strip_class_attributes: true,
            setup: function (ed) {
              ed.on('NodeChange', function (e) {
                if(e.element.tagName === "IMG"){
//                    console.log(e.element)
//                  $(e.element).wrap("<span class='d-block img__wrap' />");
                  e.element.setAttribute("class", 'img-fluid d-block');

                }
//                if(e.element.tagName == "P"){
//                  e.element.setAttribute("class", 'container mb-0');
//                }
              });
              ed.on('change', function () {
                  tinymce.triggerSave();
              });
            }

  })
}