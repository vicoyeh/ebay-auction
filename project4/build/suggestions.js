
/**
 * Provides suggestions for state names (USA).
 * @class
 * @scope public
 */
function StateSuggestions() {
    
}

var xmlHttpReq = new XMLHttpRequest();

/**
 * Request suggestions for the given autosuggest control. 
 * @scope protected
 * @param oAutoSuggestControl The autosuggest control to provide suggestions for.
 */
StateSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl /*:AutoSuggestControl*/,
                                                          bTypeAhead /*:boolean*/) {
    var sTextboxValue = oAutoSuggestControl.textbox.value;

    if (sTextboxValue) {
        var url = "suggest?q="+sTextboxValue;
        xmlHttpReq.open("GET", url);
        xmlHttpReq.onreadystatechange=this.xmlRequestHelper(xmlHttpReq,oAutoSuggestControl,bTypeAhead );
        xmlHttpReq.send(null);
    }
};

StateSuggestions.prototype.xmlRequestHelper = function(xmlHttp, oAutoSuggestControl, bTypeAhead) {
    return function() {
        if (xmlHttp.readyState==4) {
            var aSuggestions = [];
            var response = xmlHttp.responseXML.getElementsByTagName('CompleteSuggestion');

            var tmp="";
            for (i=0;i<response.length;i++) {
                tmp=response[i].childNodes[0].getAttribute('data');
                aSuggestions.push(tmp);
            }
            
            //provide suggestions to the control
            oAutoSuggestControl.autosuggest(aSuggestions, bTypeAhead);
        }   

    }
}