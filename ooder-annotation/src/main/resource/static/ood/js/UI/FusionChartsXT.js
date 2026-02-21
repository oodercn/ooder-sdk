/*
ood.UI.FusionChartsXT is a EUSUI wrap for FusionChartsXT(www.FusionCharts.com), it is NOT part of EUSUI products
If you use this widget in commercial projects, please purchase it separately
*/
ood.Class("ood.UI.FusionChartsXT","ood.UI",{
    Initialize:function(){

        // for fusioncharts in IE<=7
        if(!window.JSON)window.JSON={
            parse:function(a){return ood.unserialize(a)},
            stringify:function(a){return ood.stringify(a)}
        };
    },
    Instance:{
        initialize:function(){
        },
        refreshChart:function(dataFormat){
            return this.each(function(prf){
                if(!prf || !prf.box)return;
                prf.boxing().busy(false,'');
                if(prf.renderId){
                    var fun=function(){
                        var prop=prf.properties,t;
                        if(prf._chartId && (t=FusionCharts(prf._chartId))){
                            // dispose
                            t.dispose();
                            // clear node
                            prf.getSubNode('BOX').html("",false);
                        }

                        // new one
                        var fc=new FusionCharts(
                                prop.chartType, 
                                prf._chartId, 
                                prf.$isEm(prop.width)?prf.$em2px(prop.width):prop.width, 
                                prf.$isEm(prop.height)?prf.$em2px(prop.height):prop.height
                        ),
                         flag;
                        
                        switch(dataFormat){
                            case 'XMLUrl':
                                var xml=ood.getFileSync(prop.XMLUrl);
                                if(xml)fc.setXMLData(xml);
                            break;
                            case 'JSONUrl':
                                var json=ood.getFileSync(prop.JSONUrl);
                                if(json)fc.setJSONData(json);
                            break;
                            case 'XMLData':
                                fc.setXMLData(prop.XMLData);
                            break;
                            default:
                                if(prop.XMLUrl){
                                    var xml=ood.getFileSync(prop.XMLUrl);
                                    if(xml)fc.setXMLData(xml);
                                }else if(prop.JSONUrl){
                                    var json=ood.getFileSync(prop.JSONUrl);
                                    if(json)fc.setJSONData(json);
                                }else if(prop.XMLData){
                                    fc.setXMLData(prop.XMLData);
                                }else if(!ood.isEmpty(prop.JSONData)){
                                    flag=1;
                                    fc.setJSONData(prf.box._prepareFCData(prf,prop.JSONData));
                                }
                        }
                        // ensure cursor pointer
                        if(!flag){
                            fc.setJSONData(prf.box._prepareFCData(prf,fc.getJSONData()));
                        }
                        fc.setTransparent(true);
                        fc.render(prf.getSubNode('BOX').id());
                        // attachEvents
                        var t=FusionCharts(prf._chartId),
                            f1=function(a,argsMap){
                                if(prf.onDataClick){
                                   var sourceData, datas= a.sender.options.dataSource.data;
                                    if (datas){
                                        ood.arr.each(datas,function (data) {
                                            if (data.id=a.data.id){
                                                sourceData=data;
                                            }
                                        })
                                    }
                                    prf.boxing().onDataClick(prf,argsMap,sourceData)
                                }
                            },f2=function(a,argsMap){
                                if(prf.onLabelClick){
                                    prf.boxing().onLabelClick(prf,argsMap);
                                }
                            },f3=function(a,argsMap){
                                if(prf.onAnnotationClick)prf.boxing().onAnnotationClick(prf,argsMap);
                            };

                        if(prf._f1)t.removeEventListener("dataplotClick",prf._f1);
                        if(prf._f2)t.removeEventListener("dataLabelClick",prf._f2);
                        if(prf._f3)t.removeEventListener("annotationClick",prf._f3);
                        
                        t.addEventListener("dataplotClick",prf._f1=f1);
                        t.addEventListener("dataLabelClick",prf._f2=f1);
                        t.addEventListener("annotationClick",prf._f3=f1);

                        prf.boxing().free();
                    };
                    ood.resetRun('ood.UI.FusionChartsXT:'+prf.$xid,fun, 200);
                }
            });
        },
        setTransparent:function(isTransparent){
           return this.each(function(prf){
               var t;
               ood.set(prf.properties,["JSONData","chart","bgalpha"], isTransparent?"0,0":"");
               if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId))){
                   t.setTransparent(isTransparent);
               }
           });
        },
        getChartAttribute:function(key){
            var prf=this.get(0);
            return ood.isStr(key)?ood.get(prf.properties,["JSONData","chart",key]):ood.get(prf.properties,["JSONData","chart"]);
        },
        setChartAttribute:function(key,value){
            var h={};
            if(ood.isStr(key)){
                h[key]=value;
            }else h=key;
                
            return this.each(function(prf){
                var t;
                if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId))){
                    t.setChartAttribute(h);
                    // refresh memory in ood from real
                    ood.set(prf.properties,["JSONData","chart"], t.getChartAttribute());
                }else{
                    // reset memory in ood only 
                    var opt=ood.get(prf.properties,["JSONData","chart"]);
                    if(opt)ood.merge(opt, h, 'all');
                }
            });
        },
        getFCObject:function(){
            var prf=this.get(0);
            return prf.renderId && prf._chartId && FusionCharts(prf._chartId);
        },
        getSVGString:function(){
            var prf=this.get(0), o=prf.renderId && prf._chartId && FusionCharts(prf._chartId);
            return o?o.getSVGString():null;
        },

        updateCategories:function(data,index){
            this.each(function(prf){
                var JSONData=prf.properties.JSONData;
                data=ood.clone(data);
                JSONData.categories=data;
            });
            return this.refreshChart();
        },

        updateLine:function(data,index){
            this.each(function(prf){
                var JSONData=prf.properties.JSONData;
                data=ood.clone(data);
                if(ood.isArr(data) && ood.isArr(data[0])){
                    JSONData.lineset=data;
                }else if('lineset' in JSONData){
                    ood.set(JSONData,["lineset",index||0,"data"],data);
                }
            });
            return this.refreshChart();
        },

        updateData:function(data,index){
            this.each(function(prf){
                var JSONData=prf.properties.JSONData;
                data=ood.clone(data);
                if(ood.isArr(data) && ood.isArr(data[0])){
                    if('dataset' in JSONData){
                        JSONData.dataset=data;
                    }else{
                        JSONData.data=data[0];
                    }
                }else {
                    if('dataset' in JSONData){
                        ood.set(JSONData,["dataset",index||0,"data"],data);
                    }else{
                        JSONData.data=data;
                    }
                }
            });
            return this.refreshChart();
        },


        fillData:function(data,index,isLineset){
            this.each(function(prf){
                var JSONData=prf.properties.JSONData;
                data=ood.clone(data);
                if(ood.isArr(data) && ood.isArr(data[0])){
                    if(isLineset){
                        JSONData.lineset=data;
                    }else{
                        if('dataset' in JSONData){
                            JSONData.dataset=data;
                        }else{
                            JSONData.data=data[0];
                        }
                    }
                }else{
                    if(isLineset){
                        if('lineset' in JSONData){
                            ood.set(JSONData,["lineset",index||0,"data"],data);
                        }
                    }else{
                        if('dataset' in JSONData){
                            ood.set(JSONData,["dataset",index||0,"data"],data);
                        }else{
                            JSONData.data=data;
                        }
                    }
                }
            });
            return this.refreshChart();
        },
        updateData:function(index, value){
            return this.each(function(prf){
                 if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId))){
                        if(t.setData)
                            t.setData(index, value);
                }
            });
        },
        updateDataById:function(key, value){
            return this.each(function(prf){
                 if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId)))
                        if(t.setDataForId)
                            t.setDataForId(key, value);
            });
        },
        callFC:function(funName, params){
            var fc;
            if((fc=this.getFCObject())&&ood.isFun(fc[funName]))
                return fc[funName].apply(fc, params||[]);
        },
        configure:function(options){
            var prf=this.get(0),t;
            if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId))){
                t.configure(options);
            }
        },
        setTheme:function(theme){
            if(typeof theme!="string" || !theme)theme=null;
            this.each(function(o){
                if(theme!=o.theme){
                    if(theme===null)
                        delete o.theme;
                    else
                        o.theme=theme;
                }
            });
            return this.setChartAttribute("theme",theme);
        }
    },
    Static:{
        _objectProp:{JSONData:1,configure:1,plotData:1,feedData:1},
        Appearances:{
            KEY:{
                overflow:'hidden',
                'background-color':'var(--ood-bg)',
                'border':'1px solid var(--ood-border)'
            },
            BOX:{
                position:'absolute',
                left:0,
                top:0,
                'z-index':1,
                'background-color':'var(--ood-bg-secondary)'
            },
            COVER:{
                position:'absolute',
                left:'-1px',
                top:'-1px',
                width:0,
                height:0,
                'z-index':4,
                'background-color':'var(--ood-overlay)'
            }
        },
        Templates:{
            tagName:'div',
            className:'{_className}',
            style:'{_style}',
            BOX:{
                tagName:'div'
            },
            COVER:{
                tagName:'div',
                style:"background-image:url("+ood.ini.img_bg+");"
            }
        },
        Behaviors:{
            HotKeyAllowed:false
        },
        DataModel:{
            tabindex:null,
            expression:{
                ini:'',
                action:function () {
                }
            },
            defaultFocus:null,
            disableClickEffect:null,
            disableHoverEffect:null,
            disableTips:null,
            disabled:null,
            renderer:null,
            selectable:null,
            tips:null,
            width:{
                $spaceunit:1,
                ini:'30em'
            },
            height:{
                $spaceunit:1,
                ini:'25em'
            },
            chartCDN:"/plugins/fusioncharts/fusioncharts.js",
            chartType:{
                ini:"Column2D",
                //Single Series Charts
                listbox:["Column2D","Column3D","Line","Area2D","Bar2D","Bar3D","Pie2D","Pie3D","Doughnut2D","Doughnut3D","Pareto2D","Pareto3D",
                //Multi-series
                         "MSColumn2D","MSColumn3D","MSLine","MSBar2D","MSBar3D","MSArea","Marimekko","ZoomLine",
                //Stacked 
                         "StackedColumn3D","StackedColumn2D","StackedBar2D","StackedBar3D","StackedArea2D","MSStackedColumn2D",
                //Combination 
                         "MSCombi3D","MSCombi2D","MSColumnLine3D","StackedColumn2DLine","StackedColumn3DLine","MSCombiDY2D","MSColumn3DLineDY","StackedColumn3DLineDY","MSStackedColumn2DLineDY",
                //XYPlot
                         "Scatter","Bubble",
                //Scroll
                         "ScrollColumn2D","ScrollLine2D","ScrollArea2D","ScrollStackedColumn2D","ScrollCombi2D","ScrollCombiDY2D",
                // funnel
                        "Funnel",
               // real time
                        "RealTimeLine", "RealTimeArea", "RealTimeColumn", "RealTimeLineDY", "RealTimeStackedArea", "RealTimeStackedColumn",
               // Gauges
                        "HLinearGauge","Cylinder","HLED","VLED","Thermometer","AngularGauge",
               // others
                        "Pyramid ","Radar"//,"MultiLevelPie"
                ],
                action:function(){
                    if(this.renderId){
                        this.boxing().refreshChart();
                    }
                }
            },
            JSONData:{
                ini:{},
                get:function(){
                    var prf=this,prop=prf.properties,fc;
                    if(!ood.isEmpty(prop.JSONData))
                        return prop.JSONData;
                    else if(fc=prf.boxing().getFCObject())
                        return prf.box._cleanData(prf,fc.getJSONData());
                },
                set:function(data){
                    var prf=this,prop=prf.properties;
                    if(ood.isStr(data))data=ood.unserialize(data);
                    if(data){
                        prop.XMLData=prop.XMLUrl=prop.JSONUrl="";
                        prop.JSONData=ood.clone(data);

                        if(prf.renderId){
                            prf.boxing().refreshChart('JSONData');
                        }
                    }
                }
            },
            XMLUrl:{
                ini:"",
                set:function(url){
                    var prf=this,prop=prf.properties;

                    prop.XMLUrl=url;
                    prop.JSONUrl=prop.XMLData="";
                    prop.JSONData={};

                    if(prf.renderId){
                        prf.boxing().refreshChart('XMLUrl');
                    }
                }
            },
            XMLData:{
                ini:"",
                get:function(force){
                    var prf=this,prop=prf.properties,fc;
                    if(prop.XMLData)
                        return prop.XMLData;
                    else if(fc=prf.boxing().getFCObject())
                        return fc.getXMLData();
                },
                set:function(url){
                    var prf=this,prop=prf.properties;

                    prop.XMLData=url;
                    prop.XMLUrl=prop.JSONUrl="";
                    prop.JSONData={};

                    if(prf.renderId){
                        prf.boxing().refreshChart('XMLData');
                    }
                }
            },
            JSONUrl :{
                ini:"",
                set:function(url){
                    var prf=this,prop=prf.properties;

                    prop.JSONUrl=url;
                    prop.XMLUrl=prop.XMLData="";
                    prop.JSONData={};

                    if(prf.renderId){
                        prf.boxing().refreshChart('JSONUrl');
                    }
                }
            },
            plotData:{
                ini:{},
                get:function(data){
                    var data=this.properties.JSONData;
                    return data.dataset||data.data||{};
                },
                set:function(data){
                    var JSONData=this.properties.JSONData;
                    if(('dataset' in JSONData) || (ood.isArr(data) && ood.isArr(data[0])) )
                        JSONData.dataset=ood.clone(data);
                    else
                        JSONData.data=ood.clone(data);

                    var bak=JSONData.chart.animation;
                    JSONData.chart.animation='0';
                     this.boxing().refreshChart();
                     if(bak)JSONData.chart.animation=bak;else delete JSONData.chart.animation;
                     return this;
                }
            },
            feedData:{
                ini:"",
                set:function(data){
                    var prf=this,t;
                     if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId)) && t.feedData){
                        if(ood.isFinite(data))data="value="+data;
                        t.feedData(data||"");
                    }
                }
            }
        },
        _cleanData:function(prf,data){
            var hoder="Javascript:void(0)";
            if(data.dataset){
                ood.arr.each(data.dataset,function(o,i){
                    ood.arr.each(o.dataset,function(v,j){
                        ood.arr.each(v.data,function(w,k){
                            if(w.link==hoder)delete w.link;
                        });
                    });
                    ood.arr.each(o.data,function(v,j){
                        if(v.link==hoder)delete v.link;
                    });
                });
            }else if(data.data){
                ood.arr.each(data.data,function(o,i){
                    if(o.link==hoder)delete o.link;
                    if(o.labelLink==hoder)delete o.labelLink;
                });                
            }
            if(data.categories){
                ood.arr.each(data.categories,function(o,i){
                    ood.arr.each(o.category,function(v,j){
                       if(v.link==hoder)delete v.link;
                    });
                });
            }
            return data;
        },
        _prepareFCData:function(prf, data){
            var id=prf.$xid;
                data=ood.clone(data),
                hoder="Javascript:void(0)";
            //show cursor as pointer
            if(data.dataset){
                ood.arr.each(data.dataset,function(o,i){
                    ood.arr.each(o.dataset,function(v,j){
                        ood.arr.each(v.data,function(w,k){
                            if(!w.link)w.link=hoder;
                        });
                    });
                    ood.arr.each(o.data,function(v,j){
                       if(!v.link)v.link=hoder;
                    });
                });
            }else if(data.data){
                ood.arr.each(data.data,function(o,i){
                    if(!o.link)o.link=hoder;
                    if(!o.labelLink)o.labelLink=hoder;
                });                
            }
            if(data.categories){
                ood.arr.each(data.categories,function(o,i){
                    ood.arr.each(o.category,function(v,j){
                       if(!v.link)v.link=hoder;
                    });
                });
            }
            return data;
        },
        RenderTrigger:function(){
            var prf=this,prop=prf.properties;
            var fun=function(){
                if(!prf || !prf.box)return;

                // give chart dom id
                prf._chartId="FC_"+prf.properties.chartType+"_"+prf.$xid;

                if(!ood.isEmpty(prf.properties.configure)){
                    prf.boxing().setConfigure(prf.properties.configure, true);
                }
                if(prf.theme)
                    prf.boxing().setTheme(prf.theme);
                // render it
                prf.boxing().refreshChart();
                
                // set before destroy function
                (prf.$beforeDestroy=(prf.$beforeDestroy||{}))["unsubscribe"]=function(){
                    var t;
                    if(this._chartId && (t=FusionCharts(this._chartId))){
                        t.removeEventListener("dataplotClick",prf._f1);
                        t.removeEventListener("dataLabelClick",prf._f2);
                        t.removeEventListener("annotationClick",prf._f3);
                        prf._f1=prf._f2=prf._f3=null;
                        t.dispose();
                    }
                }
            };

            if(window.FusionCharts)fun();
            else{
                prf.boxing().busy(false, "Loading charts ...");
                ood.include("FusionCharts",prop.chartCDN,function(){
                    if(prf && prf.box){
                        prf.boxing().free();
                        fun();
                    }
                },null,false,{cache:true});
            }
        },
        EventHandlers:{
            onFusionChartsEvent:function(profile, eventObject, argumentsObject){},
            onDataClick:function(profile, argsMap,sourceData){},
            onLabelClick:function(profile, argsMap){},
            onAnnotationClick:function(profile, argsMap){},
            onShowTips:null
        },
        _onresize:function(prf,width,height){
            var size = prf.getSubNode('BOX').cssSize(),
                prop=prf.properties,
                // compare with px
                us = ood.$us(prf),
                adjustunit = function(v,emRate){return prf.$forceu(v, us>0?'em':'px', emRate)},
                root = prf.getRoot(),
                
                // caculate by px
                ww=width?prf.$px(width):width, 
                hh=height?prf.$px(height):height,
                t;

            if( (width && !ood.compareNumber(size.width,ww,6)) || (height && !ood.compareNumber(size.height,hh,6)) ){
                // reset here
                if(width)prop.width=adjustunit(ww);
                if(height)prop.height=adjustunit(hh);

                size={
                    width:width?prop.width:null,
                    height:height?prop.height:null
                };
                prf.getSubNode('BOX').cssSize(size,true);
                if(prf.$inDesign || prop.cover){
                    prf.getSubNode('COVER').cssSize(size,true);
                }
                if(prf.renderId && prf._chartId && (t=FusionCharts(prf._chartId))){
                    // ensure by px
                    t.resizeTo(ww||void 0, hh||void 0);
                }
            }
        }
    }
});