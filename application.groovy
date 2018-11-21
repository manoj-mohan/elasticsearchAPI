import com.ttn.elasticsearchAPI.dto.ResponseDTO
import com.ttn.elasticsearchAPI.util.ResponseCode
import groovy.json.JsonSlurper
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.servlet.ModelAndView
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

elasticsearch {
    url = "localhost"
    port = 9300
    timeouts {
        connect = 5000
        socket = 60000
        retry = 60000
    }
}


api {
    post {
        search {
            uri = "/search"
            operation {
                path = "/raceclips/_search"
                query = """{
                              "_source": [
                                "name",
                                "raceId",
                                "hlsURL",
                                "replayImgURL"
                              ],
                              "query": {
                                "dis_max": {
                                  "queries": [
                                    {
                                      "multi_match": {
                                        "query": "##SEARCH_QUERY##",
                                        "fields": [
                                          "name",
                                          "meeting.name",
                                          "meeting.location"
                                        ],
                                        "minimum_should_match": 2,
                                        "fuzziness": 0,
                                        "boost": 55
                                      }
                                    },
                                    {
                                      "multi_match": {
                                        "query": "##SEARCH_QUERY##",
                                        "fields": [
                                          "name",
                                          "meeting.name",
                                          "meeting.location"
                                        ],
                                        "type": "phrase_prefix", 
                                        "minimum_should_match": 2,
                                        "prefix_length": 2,
                                        "boost": 7
                                      }
                                    },
                                    {
                                      "multi_match": {
                                        "query": "##SEARCH_QUERY##",
                                        "fields": [
                                          "name",
                                          "meeting.name",
                                          "meeting.location"
                                        ],
                                        "minimum_should_match": 2,
                                        "fuzziness": "AUTO",
                                        "prefix_length": 2,
                                        "boost": 3
                                      }
                                    },
                                    {
                                      "nested": {
                                        "path": "runners",
                                        "score_mode": "avg",
                                        "query": {
                                          "multi_match": {
                                            "query": "##SEARCH_QUERY##",
                                            "fields": [
                                              "runners.name",
                                              "runners.trainer.name",
                                              "runners.jockey.name"
                                            ],
                                            "minimum_should_match": 2,
                                            "fuzziness": 0,
                                            "prefix_length": 2,
                                            "boost": 5
                                          }
                                        }
                                      }
                                    },
                                    {
                                      "nested": {
                                        "path": "runners",
                                        "score_mode": "avg",
                                        "query": {
                                          "multi_match": {
                                            "query": "##SEARCH_QUERY##",
                                            "fields": [
                                              "runners.name",
                                              "runners.trainer.name",
                                              "runners.jockey.name"
                                            ],
                                            "minimum_should_match": 2,
                                            "fuzziness": "AUTO",
                                            "prefix_length": 2
                                          }
                                        }
                                      }
                                    }
                                  ]
                                }
                              },
                              "sort": [
                                {
                                  "_score": {
                                    "order": "desc"
                                  }
                                },
                                {
                                  "startTime": {
                                    "order": "desc"
                                  }
                                }
                              ],
                              "size": ##MAX##,
                              "from": ##OFFSET##
                            }"""
                responseFilters = "took,hits.total,hits.hits._source"
            }
            processors {
                pre {
                    json = { HttpServletRequest request, HttpServletResponse response, Object handlerRequestMethod ->
                        return true
                    }
                }
                post {
                    json = { ResponseDTO body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response ->
                        String responseText = body.getSearchResponse()
                         responseText = StringUtils.replace(responseText,/"raceId"/,/"id"/)
                         responseText = StringUtils.replace(responseText,/"hlsURL"/,/"hlsConsumerURL"/)
                         responseText = StringUtils.replace(responseText,/"name"/,/"title"/)
                         responseText = StringUtils.replace(responseText,/"replayImgURL"/,/"image"/)
                        def responseJSON = new JsonSlurper().parseText(responseText)
                        ResponseCode responseCode = (body.status.statusCode == HttpStatus.OK.value()) ? (responseJSON.hits.total as int ? ResponseCode.SUCCESS : ResponseCode.NO_DATA_FOUND) : (ResponseCode.ERROR)

                        Map modifiedResponse = [
                                data   : [
                                        totalCount: responseJSON.hits.total,
                                        max       : body.max,
                                        offset    : body.offset,
                                        items     : responseJSON.hits.hits*._source.collect {
                                            it."isHD" = false
                                            it."contentType" = "RACE"
                                            it

                                        }
                                ],
                                message: responseCode.message,
                                code   : responseCode.ordinal
                        ]
                        modifiedResponse
                    }
                }
            }
        }
    }
    get {
        autocomplete {
            uri = "/autocomplete"
            requestType = "GET"
            operation {

            }
            processors {
                pre {
                    json = { HttpServletRequest request, HttpServletResponse response, Object handlerRequestMethod ->
                        return true
                    }
                }
                post {
                    json = { HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndViewRequestMethod ->

                    }
                }
            }
        }
    }
}

