using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Services;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.DTO.Reservation;
using AutoMapper;

namespace RWAEvent.WebApi.Controllers
{
    [Route("api/reservation")]
    [ApiController]
    public class ReservationRestController : ControllerBase
    {
        private readonly IEventLogger _logger;
        private readonly IReservation _reservationService;
        private readonly IUser _userService;
        private readonly IEvent _eventService;
        private readonly IMapper _mapper;

        public ReservationRestController(IReservation reservationServic, IUser userService, IEvent eventService, IEventLogger logger, IMapper mapper)
        {
            _reservationService = reservationServic;
            _userService = userService;
            _eventService = eventService;
            _logger = logger;
            _mapper = mapper;
        }

        [HttpGet("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetById(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Get reservation called id:" + id);

                var env = await _reservationService.GetReservationByIdRest(id);
                if (env == null)
                {
                    _logger.Log(LogLvl.Warning, "Reservation not found with id:" + id);
                    return NotFound();
                }

                _logger.Log(LogLvl.Information, "Reservation found for id:" + id);
                return Ok(env);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, $"Error retrieving reservation id:{id}");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPost]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Create(CreateReservationDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Create reservation called dto:" + dto.ToString());

                var eventExists = await _eventService.GetEventById(dto.EventId);
                if (eventExists == null)
                {
                    return BadRequest($"Event with Id {dto.EventId} does not exist.");
                }

                var userExists = await _userService.GetUserByUsername(dto.User);
                if (userExists == null)
                {
                    return BadRequest($"User {dto.User} does not exist.");
                }

                int id = await _reservationService.CreateReservationRest(dto, dto.User);

                _logger.Log(LogLvl.Information, "Reservation created with id: " + id);
                return Ok("Reservation created with id: " + id);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error creating reservation");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPatch]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Update(EditReservationDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Update reservation called dto:" + dto.ToString());

                var eventExists = await _eventService.GetEventById(dto.EventId);
                if (eventExists == null)
                {
                    return BadRequest($"Event with Id {dto.EventId} does not exist.");
                }

                var userExists = await _userService.GetUserByUsername(dto.User);
                if (userExists == null)
                {
                    return BadRequest($"User {dto.User} does not exist.");
                }

                bool updated = await _reservationService.UpdateReservationRest(dto);
                if (!updated)
                {
                    return NotFound($"Reservation with Id {dto.Id} does not exist.");
                }

                _logger.Log(LogLvl.Information, "Reservation updated");
                return Ok();
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error updating reservation");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpDelete("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Delete reservation called id:" + id);

                bool deleted = await _reservationService.DeleteReservation(id);

                if (!deleted)
                {
                    _logger.Log(LogLvl.Warning, "Reservation don't exist id:" + id);
                    return NotFound();

                }

                _logger.Log(LogLvl.Information, "Delete reservation called id:" + id);
                return Ok("Reservation deleted with id: " + id);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error deleting reservation");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpGet("search")]
        [AllowAnonymous]
        public async Task<IActionResult> Search(string? user, string? search, int page = 1, int pageSize = 6)
        {
            try
            {
                var filter = new ReservationFilter
                {
                    User = user,
                    Search = search,
                    Page = page,
                    PageSize = pageSize
                };

                var reservations = await _reservationService.GetReservationsByFilter(filter);

                if (reservations == null || reservations.Count == 0)
                    return NotFound();

                var restResult = new ReservationRestResultDTO
                {
                    TotalCount = reservations.Count,
                    Reservations = _mapper.Map<List<ReservationPreviewDTO>>(reservations)
                };

                return Ok(restResult);
            }
            catch (Exception ex)
            {
                _logger.Log(LogLvl.Error, "Error searching reservations");
                return StatusCode(StatusCodes.Status500InternalServerError, ex.Message);
            }
        }

    }
}
