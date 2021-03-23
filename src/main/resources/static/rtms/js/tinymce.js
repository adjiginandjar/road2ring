function editor_textarea() {
    tinymce.init({
        selector: ".mce-editor",
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
        file_picker_callback: function (cb, value, meta) {
            var input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');

            input.onchange = function () {
                var file = this.files[0];
                var reader = new FileReader();
                reader.onload = function () {
                    var hostname = $(location).attr('protocol') + '//' + $(location).attr('host');
                    var api = hostname + "/si/api";
                    var formData = new FormData();
                    formData.append("file", file)
                    var json_data = new XMLHttpRequest();
                    var url = api + '/trip/upload_trip/potrait';
                    var data;

                    json_data.onreadystatechange = function () {
                        if (this.readyState == 4 && this.status == 200) {
                            var response = JSON.parse(this.responseText);
                            data = response.object.original;
                            cb(data, { title: file.name });
                        }
                    };

                    json_data.open("POST", url, true);
                    json_data.send(formData);
                };
                reader.readAsDataURL(file);
            };

            input.click();
        },
        valid_elements: '+*[*]',

        media_live_embeds: true,
        paste_auto_cleanup_on_paste: true,
        paste_remove_styles: true,
        paste_remove_styles_if_webkit: true,
        paste_strip_class_attributes: true,
        setup: function (ed) {
            ed.ui.registry.addButton('inlineimage', {
                title: 'Add Image',
                icon: 'image',
            });
        },
    });

}