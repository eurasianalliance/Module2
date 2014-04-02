/*
 * main_bitmap.c
 *
 *  Created on: 2014-03-25
 *      Author: Yoji
 */
#include <stdio.h>
#include "altera_up_avalon_video_pixel_buffer_dma.h"
#include "altera_up_avalon_video_character_buffer_with_dma.h"
#include "altera_up_avalon_audio_and_video_config.h"
#include "altera_up_avalon_audio.h"
#include "altera_up_sd_card_avalon_interface.h"

#include "sys/alt_timestamp.h"
#include "sys/alt_alarm.h"
#include "sys/alt_irq.h"

#include "bitmap_platform.h"
#include "sound_platform.h"

int main()
{
//-----------------------------------------------------CONFIG STUFF------------------------------------------------------------

	//--------Audio----------------------------------------------------
	    printf("SDCARD CONNECTION CHECK\n");
	    alt_up_sd_card_dev *device_reference=alt_up_sd_card_open_dev("/dev/Altera_UP_SD_Card_Avalon_Interface_0");
	    alt_up_audio_dev *audio;
	    sdcardcheck(device_reference);

	    printf("AV CONFIG SETUP\n");
	    av_config_setup();

	    printf("AUDIO OPEN\n");
	    audio=alt_up_audio_open_dev("/dev/audio");

	//--------------------Pixel Buffer-------------------------------------------------
		  //define pixel_buffer
		  alt_up_pixel_buffer_dma_dev* pixel_buffer;

		  // Use the name of your pixel buffer DMA core
		  pixel_buffer = alt_up_pixel_buffer_dma_open_dev("/dev/pixel_buffer_dma");

		  //Define buffer addresses
		  unsigned int pixel_buffer_addr1 = PIXEL_BUFFER_BASE;
		  unsigned int pixel_buffer_addr2 = PIXEL_BUFFER_BASE + (320 * 240 * 2);

		  //Set the first buffer address
		 alt_up_pixel_buffer_dma_change_back_buffer_address(pixel_buffer,pixel_buffer_addr1);

		  // Swap background and foreground buffers
		  alt_up_pixel_buffer_dma_swap_buffers(pixel_buffer);
		 // Wait for the swap to complete
		  while(alt_up_pixel_buffer_dma_check_swap_buffers_status(pixel_buffer));

		  //Set the 2nd buffer address
		 alt_up_pixel_buffer_dma_change_back_buffer_address(pixel_buffer,pixel_buffer_addr2);

		// Clear the screen
		  alt_up_pixel_buffer_dma_clear_screen(pixel_buffer, 0);
		  alt_up_pixel_buffer_dma_clear_screen(pixel_buffer, 1);

		  alt_up_char_buffer_dev *char_buffer;
		  char_buffer = alt_up_char_buffer_open_dev("/dev/char_drawer");
		  alt_up_char_buffer_init(char_buffer);

//-----------------------------------------------------VGA STUFF------------------------------------------------------------

	//pictures will be okay as they overlap each other
	//title
	draw_title(pixel_buffer);

	//doom device is with fire
	draw_doomdevice(pixel_buffer);
	draw_fire_sides(pixel_buffer);

	//lightning attack
	draw_light_sides(pixel_buffer);

	//faces
	draw_face(pixel_buffer);
	draw_hurtface(pixel_buffer);
	draw_deadface(pixel_buffer);

	//health bars
	full_health_bar(pixel_buffer);
	health_bar_min1(pixel_buffer);
	health_bar_min2(pixel_buffer);
	health_bar_min3(pixel_buffer);
	health_bar_min4(pixel_buffer);
	health_bar_min5(pixel_buffer);

//-----------------------------------------------------SOUND STUFF------------------------------------------------------------

	    //---CLING SONG---//
	    unsigned int buffer[CLING_L];
	    char *filename="cling.wav";
	    short int handle;

	    //must have the songs read beforehand
	    read_audio(buffer, filename, handle);

	    //do the play_audio in the respective areas
	    play_audio(audio, buffer, handle);

	    //---CCHAT SONG---//
	    unsigned int cchat_buf[CCHAT_L];
	    char *cchat_name="cchat.wav";
	    short int cchat_handle;

	    //must have the songs read beforehand
	    read_cchat(cchat_buf, cchat_name, cchat_handle);

	    //do the play_audio in the respective areas
	    //play_cchat(audio, cchat_buf, cchat_handle);

	    //---PDEATH SONG---//
	    unsigned int pdeath_buf[PDEATH_L];
	    char *pdeath_name="pdeath.wav";
	    short int pdeath_handle;

	    //must have the songs read beforehand
	    read_pdeath(pdeath_buf, pdeath_name, pdeath_handle);

	    //do the play_audio in the respective areas
	    //play_pdeath(audio, pdeath_buf, pdeath_handle);

	    //---PEW SONG---//
	    unsigned int pew_buf[PEW_L];
	    char *pew_name="pew.wav";
	    short int pew_handle;

	    //must have the songs read beforehand
	    read_pew(pew_buf, pew_name, pew_handle);

	    //do the play_audio in the respective areas
	    //play_pew(audio, pew_buf, pew_handle);

	    //---WUBBLE SONG---//
	    unsigned int wub_buf[WUBBLE_L];
	    char *wub_name="wubble.wav";
	    short int wub_handle;

	    //must have the songs read beforehand
	    read_wub(wub_buf, wub_name, wub_handle);

	    //do the play_audio in the respective areas
	    //play_wub(audio, wub_buf, wub_handle);

	    alt_up_sd_card_fclose(filename); //cling sound
	    alt_up_sd_card_fclose(cchat_name);
	    alt_up_sd_card_fclose(pdeath_name);
	    alt_up_sd_card_fclose(pew_name);
	    alt_up_sd_card_fclose(wub_name);
}
