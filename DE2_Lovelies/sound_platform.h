/*
 * sound_platform.h
 *
 *  Created on: 2014-04-01
 *      Author: Yoji
 */

#ifndef SOUND_PLATFORM_H_
#define SOUND_PLATFORM_H_
#include <stdio.h>
#include <alt_types.h>
#include <altera_up_sd_card_avalon_interface.h>
#include "altera_up_avalon_video_pixel_buffer_dma.h"
#include "altera_up_avalon_video_character_buffer_with_dma.h"
#include "sys/alt_timestamp.h"
#include "sys/alt_alarm.h"
#include <sys/alt_cache.h>
#include <stdlib.h>
#include "altera_up_avalon_audio_and_video_config.h"
#include "altera_up_avalon_audio.h"
#include "sys/alt_irq.h"

//--------------Constants------------------------------------------------
//7720/200 of coin length and size
//song length constants
#define CCHAT_L 33762
#define CLING_L 26505
#define PDEATH_L 121767
#define PEW_L 19116
#define WUBBLE_L 25934

//song size constants
#define CCHAT_S 874
#define CLING_S 686
#define PDEATH_S 3154
#define PEW_S 495
#define WUBBLE_S 671

//--------------||Functions||----------------//

//---------Sound Functions------------------------------------------
void av_config_setup(void);
void sdcardcheck(alt_up_sd_card_dev *device_reference);
int read_audio(unsigned int *buffer, char* filename, short int handle); //this is cling
void play_audio(alt_up_audio_dev *audio, unsigned int *buffer, short int handle);
int read_cchat(unsigned int *cchat_buf, char* cchat_name, short int cchat_handle);
void play_cchat(alt_up_audio_dev *audio, unsigned int *cchat_buf, short int cchat_handle);

int read_pdeath(unsigned int *pdeath_buf, char* pdeath_name, short int pdeath_handle);
void play_pdeath(alt_up_audio_dev *audio, unsigned int *pdeath_buf, short int pdeath_handle);
int read_pew(unsigned int *pew_buf, char* pew_name, short int pew_handle);
void play_pew(alt_up_audio_dev *audio, unsigned int *pew_buf, short int pew_handle);
int read_wub(unsigned int *wub_buf, char* wub_name, short int wub_handle);
void play_wub(alt_up_audio_dev *audio, unsigned int *wub_buf, short int wub_handle);

#endif /* SOUND_PLATFORM_H_ */
