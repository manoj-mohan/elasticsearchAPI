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
                                "replayImgURL",
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
                                        "path": "runner",
                                        "score_mode": "avg",
                                        "query": {
                                          "multi_match": {
                                            "query": "##SEARCH_QUERY##",
                                            "fields": [
                                              "runner.name",
                                              "runner.trainer.name",
                                              "runner.jockey.name"
                                            ],
                                            "minimum_should_match": 2,
                                            "fuzziness": 0,
                                            "prefix_length": 2,
                                            "boost": 5
                                          }
                                        },
                                        "inner_hits": {
                                          "_source": [
                                            "runner.name",
                                            "runner.trainer.name",
                                            "runner.jockey.name"
                                          ]
                                        }
                                      }
                                    },
                                    {
                                      "nested": {
                                        "path": "runner",
                                        "score_mode": "avg",
                                        "query": {
                                          "multi_match": {
                                            "query": "##SEARCH_QUERY##",
                                            "fields": [
                                              "runner.name",
                                              "runner.trainer.name",
                                              "runner.jockey.name"
                                            ],
                                            "minimum_should_match": 2,
                                            "fuzziness": "AUTO",
                                            "prefix_length": 2
                                          }
                                        },
                                        "inner_hits": {
                                          "_source": [
                                            "runner.name",
                                            "runner.trainer.name",
                                            "runner.jockey.name"
                                          ]
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
                        def responseJSON = new JsonSlurper().parseText(body.getSearchResponse())
                        ResponseCode responseCode = (body.status.statusCode == HttpStatus.OK.value()) ? (responseJSON.hits.total as int ? ResponseCode.SUCCESS : ResponseCode.NO_DATA_FOUND) : (ResponseCode.ERROR)
                        Map modifiedResponse = [
                                data   : [
                                        totalCount: responseJSON.hits.total,
                                        max       : body.max,
                                        offset    : body.offset,
                                        items     : responseJSON.hits.hits
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

