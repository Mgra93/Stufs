using AutoMapper;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;

namespace RWAEvent.WebApi.Controllers
{
    [Route("api/event")]
    [ApiController]
    public class EventRestController : ControllerBase
    {
        private readonly IEventLogger _logger;
        private readonly IEvent _eventService;
        private readonly IEventType _eventTypeService;
        private readonly IPerformer _performerService;
        private readonly IMapper _mapper;

        public EventRestController(IEvent eventService, IEventType eventTypeService, IPerformer performerService, IEventLogger logger, IMapper mapper)
        {
            _eventService = eventService;
            _eventTypeService = eventTypeService;
            _performerService = performerService;
            _logger = logger;
            _mapper = mapper;
        }

        [HttpGet("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetById(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Get event called id:" + id);

                var eve = await _eventService.GetEventByIdRest(id);

                if (eve == null)
                {
                    _logger.Log(LogLvl.Warning, "Event not found with id:" + id);
                    return NotFound();
                }

                _logger.Log(LogLvl.Information, "Event found for id:" + id);
                return Ok(eve);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error retrieving event with id:" + id);
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPost]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Create(CreateEventDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Create event called dto:" + dto.ToString());

                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }

                var performersExist = await _performerService.PerformersExist(dto.PerformerList);

                if (!performersExist)
                {
                    return BadRequest("One or more performers not exist");
                }

                var eventType = await _eventTypeService.GetEventTypeById(dto.EventTypeId);

                if (eventType == null)
                {
                    return BadRequest("Event type don't exist");
                }


                int id = await _eventService.CreateEventRest(dto);

                _logger.Log(LogLvl.Information, "Event created, new id: " + id);

                return Ok("Event created, new id: " + id);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error creating event");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }


        [HttpPatch]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Update(UpdateEventDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Update event called dto:" + dto.ToString());

                var eventExists = await _eventService.GetEventById(dto.Id);

                if (eventExists == null)
                {
                    return BadRequest($"Event {dto.Id} not found");
                }

                var eventTypeExist = await _eventTypeService.GetEventTypeById(dto.EventTypeId);

                if (eventTypeExist == null)
                {
                    return BadRequest($"Event type {dto.EventTypeId} not found");
                }

                var performersExist = await _performerService.PerformersExist(dto.PerformerList);

                if (!performersExist)
                {
                    return BadRequest("One or more performers do not exist or are inactive.");
                }

                bool updated = await _eventService.UpdateEventRest(dto);


                _logger.Log(LogLvl.Information, "Event updated id:" + dto.Id);
                return Ok("Event updated with id:" + dto.Id);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error creating event");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }

        }

        [HttpDelete("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Delete event called id:" + id);

                bool deleted = await _eventService.DeleteEvent(id);

                if (!deleted)
                {
                    _logger.Log(LogLvl.Warning, "Event don't exist id:" + id);
                    return NotFound();

                }
                _logger.Log(LogLvl.Information, "Delete event called dto:" + id);
                return Ok();
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error deleting event");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpGet("search")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Search(string? search, int page = 1, int pageSize = 6, int? eventTypeId = null)
        {
            try
            {
                var filter = new EventFilter
                {
                    Search = search,
                    Page = page,
                    PageSize = pageSize,
                    EventTypeId = eventTypeId
                };

                var result = await _eventService.GetEventsByFilter(filter);

                if (result.Events == null || result.Events.Count == 0)
                    return NotFound();

                var restResult = new EventRestResultDTO
                {
                    TotalCount = result.TotalCount,
                    Events = _mapper.Map<List<EventPreviewDTO>>(result.Events)
                };

                return Ok(restResult);
            }
            catch (Exception ex)
            {
                _logger.Log(LogLvl.Error, "Error searching event");
                return StatusCode(StatusCodes.Status500InternalServerError, ex.Message);
            }
        }
    }
}
